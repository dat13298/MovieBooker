package com.datnt.moviebooker.service;

import com.datnt.moviebooker.entity.Movie;
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
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String MOVIE_CACHE_KEY = "movies";

    public Page<Movie> getAllMovies(Pageable pageable) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        // get all movies from cache
        List<Movie> cachedMovies = hashOps.entries(MOVIE_CACHE_KEY).values().stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, Movie.class);
                    } catch (Exception e) {
                        logger.error("Error parsing movie from cache", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        // if cache is empty, get all movies from database and save to cache
        if (cachedMovies.isEmpty()) {
            List<Movie> movies = movieRepository.findAll();
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

    public Movie createMovie(Movie movie) {
        Movie savedMovie = movieRepository.save(movie);
        updateMovieCache(savedMovie);// add to cache
        return savedMovie;
    }

    public Movie updateMovie(Long id, Movie updatedMovie) {
        return movieRepository.findById(id).map(movie -> {
            movie.setTitle(updatedMovie.getTitle());
            movie.setDescription(updatedMovie.getDescription());
            movie.setDuration(updatedMovie.getDuration());
            Movie savedMovie = movieRepository.save(movie);
            updateMovieCache(savedMovie);// update cache
            return savedMovie;
        }).orElse(null);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
        redisTemplate.opsForHash().delete(MOVIE_CACHE_KEY, id.toString());// delete from cache
    }

    public Movie findById(Long id) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        // get movie from cache
        String cachedMovie = hashOps.get(MOVIE_CACHE_KEY, id.toString());

        // if cache is not empty, return movie
        if (cachedMovie != null) {
            try {
                return objectMapper.readValue(cachedMovie, Movie.class);
            } catch (Exception e) {
                logger.error("Error parsing movie from cache", e);
            }
        }

        // if cache is empty, get movie from database and save to cache
        return  movieRepository.findById(id).map(movie -> {
            try {
                hashOps.put(MOVIE_CACHE_KEY, id.toString(), objectMapper.writeValueAsString(movie));
            } catch (Exception e) {
                logger.error("Error saving movie to cache", e);
            }
            return movie;
        }).orElse(null);
    }

    private void updateMovieCache(Movie movie) {
        try {
            // save to cache with key is movie id
            redisTemplate.opsForHash().put(MOVIE_CACHE_KEY, movie.getId().toString(), objectMapper.writeValueAsString(movie));
            redisTemplate.expire(MOVIE_CACHE_KEY, 10, TimeUnit.MINUTES);// set expire time
        } catch (Exception e) {
            logger.error("Error saving movie to cache", e);
        }
    }

    private Page<Movie> paginateList(List<Movie> movies, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), movies.size());
        return new PageImpl<>(movies.subList(start, end), pageable, movies.size());// paginate list
    }
}