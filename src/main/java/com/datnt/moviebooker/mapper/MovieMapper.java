package com.datnt.moviebooker.mapper;

import com.datnt.moviebooker.dto.MovieRequest;
import com.datnt.moviebooker.dto.MovieResponse;
import com.datnt.moviebooker.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequest req) {
        return Movie.builder()
                .movieName(req.getTitle())
                .description(req.getDescription())
                .duration(req.getDuration())
                .rating(req.getRating())
                .releaseDate(req.getReleaseDate())
                .director(req.getDirector())
                .actors(req.getActors())
                .movieType(req.getMovieType())
                .language(req.getLanguage())
                .premiereDate(req.getPremiereDate())
                .movieStatus(req.getMovieStatus())
                .screenType(req.getScreenType())
                .movieCode(req.getMovieCode())
                .trailerUrl(req.getTrailerUrl())
                .eighteenPlus(req.getIs18Plus())
                .imageUrl(null)
                .build();
    }


    public MovieResponse toResponse(Movie m) {
        return MovieResponse.builder()
                .id(m.getId())
                .title(m.getMovieName())
                .description(m.getDescription())
                .duration(m.getDuration())
                .rating(m.getRating())
                .imageUrl(m.getImageUrl())
                .director(m.getDirector())
                .actors(m.getActors())
                .movieType(m.getMovieType())
                .language(m.getLanguage())
                .premiereDate(m.getPremiereDate())
                .movieStatus(m.getMovieStatus())
                .screenType(m.getScreenType())
                .eighteenPlus(m.isEighteenPlus())
                .movieCode(m.getMovieCode())
                .trailerUrl(m.getTrailerUrl())
                .releaseDate(m.getReleaseDate())
                .build();
    }

    public void updateEntity(Movie movie, MovieRequest req) {
        movie.setMovieName(req.getTitle());
        movie.setDescription(req.getDescription());
        movie.setDuration(req.getDuration());
        movie.setRating(req.getRating());
        movie.setReleaseDate(req.getReleaseDate());
        movie.setDirector(req.getDirector());
        movie.setActors(req.getActors());
        movie.setMovieType(req.getMovieType());
        movie.setLanguage(req.getLanguage());
        movie.setPremiereDate(req.getPremiereDate());
        movie.setMovieStatus(req.getMovieStatus());
        movie.setScreenType(req.getScreenType());
        movie.setEighteenPlus(req.getIs18Plus());
        movie.setMovieCode(req.getMovieCode());
        movie.setTrailerUrl(req.getTrailerUrl());
    }
}
