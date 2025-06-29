package com.datnt.moviebooker.service;

import com.datnt.moviebooker.constant.MovieStatus;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    private static final TypeReference<List<MovieResponse>> MOVIE_LIST_TYPE = new TypeReference<>() {
    };
    private static final long MOVIE_CACHE_TTL = 10L;

    public Page<MovieResponse> getAllMovies(Pageable pageable,
                                            String keyword,
                                            String screenType,
                                            Boolean is18Plus,
                                            MovieStatus status) {

        boolean hasFilter =
                (keyword != null && !keyword.isBlank()) ||
                        (screenType != null && !screenType.isBlank()) ||
                        (is18Plus != null) ||
                        (status != null);

        if (hasFilter) {
            return movieRepository
                    .search(
                            keyword == null ? "" : keyword.trim(),
                            status,
                            screenType,
                            is18Plus,
                            pageable)
                    .map(movieMapper::toResponse);
        }

        String json = redisTemplate.opsForValue().get(MOVIE_CACHE_KEY);
        List<MovieResponse> list;
        try {
            if (json != null) {
                list = objectMapper.readValue(json, MOVIE_LIST_TYPE);
            } else {
                list = movieRepository.findAll()
                        .stream().map(movieMapper::toResponse).toList();
                redisTemplate.opsForValue().set(
                        MOVIE_CACHE_KEY,
                        objectMapper.writeValueAsString(list),
                        MOVIE_CACHE_TTL, TimeUnit.MINUTES);
            }
        } catch (IOException e) {
            logger.error("Error (de)serializing movie cache", e);
            list = movieRepository.findAll()
                    .stream().map(movieMapper::toResponse).toList();
        }

        return paginateList(list, pageable);
    }


    public Optional<MovieResponse> getMovieById(Long id) {
        String cachedMoviesJson = redisTemplate.opsForValue().get(MOVIE_CACHE_KEY);

        if (cachedMoviesJson != null) {
            try {
                List<MovieResponse> movies = objectMapper.readValue(cachedMoviesJson, new TypeReference<>() {
                });
                return movies.stream().filter(m -> m.getId().equals(id)).findFirst();
            } catch (Exception e) {
                logger.error("Error parsing movie from cache", e);
            }
        }

        return movieRepository.findById(id).map(movieMapper::toResponse);
    }


    public MovieResponse createMovie(MovieRequest movieRequest) {
        String imageUrl = null;
        String publicId = null;

        try {
            // 1. Upload ảnh lên Cloudinary
            Map<String, String> uploadResult = cloudinaryService.uploadImageWithResult(movieRequest.getImage());
            imageUrl = uploadResult.get("url");
            publicId = uploadResult.get("publicId");

            // 2. Tạo entity Movie
            Movie movie = movieMapper.toEntity(movieRequest);
            movie.setImageUrl(imageUrl);

            // 3. Lưu vào DB (có thể ném lỗi)
            movieRepository.save(movie);

            MovieResponse response = movieMapper.toResponse(movie);
            evictMovieCache(response);
            return response;

        } catch (Exception e) {
            // 4. Nếu DB save lỗi => rollback ảnh đã upload
            if (publicId != null) {
                cloudinaryService.deleteImage(publicId);
            }
            throw new RuntimeException("Failed to create movie: " + e.getMessage(), e);
        }
    }


    public MovieResponse updateMovie(Long id, MovieRequest req) {
        return movieRepository.findById(id).map(movie -> {

            if (req.getImage() != null) {
                String url = cloudinaryService.uploadImage(req.getImage());
                movie.setImageUrl(url);
            }

            movieMapper.updateEntity(movie, req);

            movieRepository.save(movie);
            MovieResponse res = movieMapper.toResponse(movie);
            evictMovieCache(res);
            return res;

        }).orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
        invalidateMovieCache();
    }


    private void evictMovieCache(MovieResponse movie) {
        try {
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
