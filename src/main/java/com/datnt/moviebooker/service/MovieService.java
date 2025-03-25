package com.datnt.moviebooker.service;

import com.datnt.moviebooker.dto.MovieRequest;
import com.datnt.moviebooker.dto.MovieResponse;
import com.datnt.moviebooker.entity.Movie;
import com.datnt.moviebooker.mapper.MovieMapper;
import com.datnt.moviebooker.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
    private static final String MOVIE_CACHE_KEY = "movies";

    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        List<MovieResponse> cachedMovies = hashOps.entries(MOVIE_CACHE_KEY).values().stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, MovieResponse.class);
                    } catch (Exception e) {
                        logger.error("Error parsing movie from cache", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (cachedMovies.isEmpty()) {
            List<MovieResponse> movies = movieRepository.findAll()
                    .stream()
                    .map(movieMapper::toResponse)
                    .toList();

            movies.forEach(movie -> {
                try {
                    hashOps.put(MOVIE_CACHE_KEY, movie.getId().toString(), objectMapper.writeValueAsString(movie));
                } catch (Exception e) {
                    logger.error("Error saving movie to cache", e);
                }
            });

            redisTemplate.expire(MOVIE_CACHE_KEY, 10, TimeUnit.MINUTES);
            cachedMovies = movies;
        }

        return paginateList(cachedMovies, pageable);
    }

    public Optional<MovieResponse> getMovieById(Long id) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String cachedMovie = hashOps.get(MOVIE_CACHE_KEY, id.toString());

        if (cachedMovie != null) {
            try {
                return Optional.of(objectMapper.readValue(cachedMovie, MovieResponse.class));
            } catch (Exception e) {
                logger.error("Error parsing movie from cache", e);
            }
        }

        return movieRepository.findById(id).map(movie -> {
            MovieResponse response = movieMapper.toResponse(movie);
            try {
                hashOps.put(MOVIE_CACHE_KEY, id.toString(), objectMapper.writeValueAsString(response));
            } catch (Exception e) {
                logger.error("Error saving movie to cache", e);
            }
            return response;
        });
    }

    public MovieResponse createMovie(MovieRequest movieRequest) {
        Movie movie = movieMapper.toEntity(movieRequest);
        Movie savedMovie = movieRepository.save(movie);
        MovieResponse response = movieMapper.toResponse(savedMovie);
        updateMovieCache(response);
        return response;
    }

    public MovieResponse updateMovie(Long id, MovieRequest movieRequest) {
        return movieRepository.findById(id).map(movie -> {
            movieMapper.updateEntity(movie, movieRequest);
            Movie updatedMovie = movieRepository.save(movie);
            MovieResponse response = movieMapper.toResponse(updatedMovie);
            updateMovieCache(response);
            return response;
        }).orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
        redisTemplate.opsForHash().delete(MOVIE_CACHE_KEY, id.toString());
    }

    private void updateMovieCache(MovieResponse movie) {
        try {
            redisTemplate.opsForHash().put(MOVIE_CACHE_KEY, movie.getId().toString(), objectMapper.writeValueAsString(movie));
            redisTemplate.expire(MOVIE_CACHE_KEY, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("Error saving movie to cache", e);
        }
    }

    private Page<MovieResponse> paginateList(List<MovieResponse> movies, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), movies.size());
        return new PageImpl<>(movies.subList(start, end), pageable, movies.size());
    }
}
