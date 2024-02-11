package fr.isen.java2.db.daos;

import java.util.List;
import fr.isen.java2.db.entities.Movie;
import fr.isen.java2.db.entities.Genre;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class MovieDao {

	GenreDao genreDao;
	DataSource dataSource;
	Connection connection;

	public MovieDao()
	{
		dataSource = DataSourceFactory.getDataSource();
		genreDao = new GenreDao();
	}

	public List<Movie> listMovies() {
		List<Movie> movies = new ArrayList<Movie>();
		String prompt = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre";

		try {
			this.connection = this.dataSource.getConnection();
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery(prompt);

			while (resultSet.next())
			{
				movies.add(new Movie(
						resultSet.getInt("idmovie"),
						resultSet.getString("title"),
						resultSet.getDate("release_date").toLocalDate(),
						new Genre(resultSet.getInt("idgenre"),resultSet.getString("name")),
						resultSet.getInt("duration"),
						resultSet.getString("director"),
						resultSet.getString("summary")
				));
			}

			connection.close(); //two section are overlapped when run so have to close then reconnect again
			return movies;
		} catch (SQLException e) {
			throw new RuntimeException("SQL Error at MovieDao.listMovies()");
		}

	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new ArrayList<>();
		String prompt = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = '" + genreName +"'";

		try {
			this.connection = this.dataSource.getConnection();
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery(prompt);

			while (resultSet.next())
			{
				movies.add(new Movie(
						resultSet.getInt("idmovie"),
						resultSet.getString("title"),
						resultSet.getDate("release_date").toLocalDate(),
						new Genre(resultSet.getInt("idgenre"),resultSet.getString("name")),
						resultSet.getInt("duration"),
						resultSet.getString("director"),
						resultSet.getString("summary")
				));
			}

			connection.close(); //two section are overlapped when run so have to close then reconnect again
			return movies;
		} catch (SQLException e) {
			throw new RuntimeException("SQL Error at MovieDao.listMoviesByGenre()");
		}

	}

	public Movie addMovie(Movie movie) {
		String prompt = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";

		try {
			this.connection = this.dataSource.getConnection();
			PreparedStatement statement = this.connection.prepareStatement(prompt);

			//fill-in data
			statement.setString(1, movie.getTitle());
			statement.setObject(2,movie.getReleaseDate());
			statement.setInt(3,movie.getGenre().getId());
			statement.setInt(4,movie.getDuration());
			statement.setString(5,movie.getDirector());
			statement.setString(6,movie.getSummary());

			statement.executeUpdate();

			ResultSet ids = statement.getGeneratedKeys();

			if (!ids.next()) {
				throw new SQLException("Failed to create Movie");
			}
			movie.setId(ids.getInt(1));
			Movie returnMovie = new Movie(
					ids.getInt(1),
					movie.getTitle(),
					movie.getReleaseDate(),
					movie.getGenre(),
					movie.getDuration(),
					movie.getDirector(),
					movie.getSummary()
			);
			connection.close(); //two section are overlapped when run so have to close then reconnect again
			return returnMovie;
		} catch (SQLException e) {
			throw new RuntimeException("SQL Error at MovieDao.addMovie()");
		}

	}
}
