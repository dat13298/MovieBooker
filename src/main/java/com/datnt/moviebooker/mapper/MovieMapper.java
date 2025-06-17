package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.MovieRequest;
import com.datnt.moviebooker.dto.MovieResponse;
import com.datnt.moviebooker.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequest request) {
        return Movie.builder()
                .movieName(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .rating(request.getRating())
                .releaseDate(request.getReleaseDate())
                .director(request.getDirector())
                .actors(request.getActors())
                .movieType(request.getMovieType())
                .language(request.getLanguage())
                .premiereDate(request.getPremiereDate())
                .movieStatus(request.getMovieStatus())
                .screenType(request.getScreenType())
                .build();
    }

    public MovieResponse toResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getMovieName())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .imageUrl(movie.getImageUrl())
                .director(movie.getDirector())
                .actors(movie.getActors())
                .movieType(movie.getMovieType())
                .language(movie.getLanguage())
                .premiereDate(movie.getPremiereDate())
                .movieStatus(movie.getMovieStatus())
                .screenType(movie.getScreenType())
                .build();
    }

    public void updateEntity(Movie movie, MovieRequest request) {
        movie.setMovieName(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setDirector(request.getDirector());
        movie.setActors(request.getActors());
        movie.setMovieType(request.getMovieType());
        movie.setLanguage(request.getLanguage());
        movie.setPremiereDate(request.getPremiereDate());
        movie.setMovieStatus(request.getMovieStatus());
        movie.setScreenType(request.getScreenType());
    }
}
