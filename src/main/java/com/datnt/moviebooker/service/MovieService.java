package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.MovieRequest;
import com.datnt.moviebooker.dto.MovieResponse;
import com.datnt.moviebooker.entity.Movie;
import com.datnt.moviebooker.mapper.MovieMapper;
import com.datnt.moviebooker.repository.MovieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CloudinaryService cloudinaryService;
    private static final String MOVIE_CACHE_KEY = "movies";

    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        String cachedMoviesJson = redisTemplate.opsForValue().get(MOVIE_CACHE_KEY);

        List<MovieResponse> cachedMovies;
        if (cachedMoviesJson != null) {
            try {
                cachedMovies = objectMapper.readValue(cachedMoviesJson, new TypeReference<>() {});
            } catch (Exception e) {
                logger.error("Error parsing movie list from cache", e);
                cachedMovies = List.of();
            }
        } else {
            cachedMovies = movieRepository.findAll().stream()
                    .map(movieMapper::toResponse)
                    .toList();
            try {
                redisTemplate.opsForValue().set(MOVIE_CACHE_KEY, objectMapper.writeValueAsString(cachedMovies), 10, TimeUnit.MINUTES);
            } catch (Exception e) {
                logger.error("Error saving movie list to cache", e);
            }
        }
        return paginateList(cachedMovies, pageable);
    }


    public Optional<MovieResponse> getMovieById(Long id) {
        String cachedMoviesJson = redisTemplate.opsForValue().get(MOVIE_CACHE_KEY);

        if (cachedMoviesJson != null) {
            try {
                List<MovieResponse> movies = objectMapper.readValue(cachedMoviesJson, new TypeReference<>() {});
                return movies.stream().filter(m -> m.getId().equals(id)).findFirst();
            } catch (Exception e) {
                logger.error("Error parsing movie from cache", e);
            }
        }

        return movieRepository.findById(id).map(movieMapper::toResponse);
    }


    public MovieResponse createMovie(MovieRequest movieRequest) {
        String imageUrl = cloudinaryService.uploadImage(movieRequest.getImage());

        Movie movie = Movie.builder()
                .title(movieRequest.getTitle())
                .description(movieRequest.getDescription())
                .duration(movieRequest.getDuration())
                .imageUrl(imageUrl)
                .rating(movieRequest.getRating())
                .releaseDate(movieRequest.getReleaseDate())
                .build();

        movieRepository.save(movie);
        MovieResponse response = movieMapper.toResponse(movie);

        updateMovieCache(response);
        return response;
    }

    public MovieResponse updateMovie(Long id, MovieRequest movieRequest) {
        return movieRepository.findById(id).map(movie -> {
            if (movieRequest.getImage() != null) {
                String newImageUrl = cloudinaryService.uploadImage(movieRequest.getImage());
                movie.setImageUrl(newImageUrl);
            }
            movie.setTitle(movieRequest.getTitle());
            movie.setDescription(movieRequest.getDescription());
            movie.setDuration(movieRequest.getDuration());
            movie.setRating(movieRequest.getRating());
            movie.setReleaseDate(movieRequest.getReleaseDate());

            movieRepository.save(movie);
            MovieResponse response = movieMapper.toResponse(movie);

            updateMovieCache(response);
            return response;
        }).orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
        redisTemplate.opsForHash().delete(MOVIE_CACHE_KEY, id.toString());
        invalidateMovieCache();
    }


    private void updateMovieCache(MovieResponse movie) {
        try {
            redisTemplate.opsForHash().put(MOVIE_CACHE_KEY, movie.getId().toString(), objectMapper.writeValueAsString(movie));
            invalidateMovieCache();
        } catch (Exception e) {
            logger.error("Error saving movie to cache", e);
        }
    }

    private void invalidateMovieCache() {
        redisTemplate.delete(MOVIE_CACHE_KEY);
    }

    private Page<MovieResponse> paginateList(List<MovieResponse> movies, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), movies.size());
        return new PageImpl<>(movies.subList(start, end), pageable, movies.size());
    }
}
