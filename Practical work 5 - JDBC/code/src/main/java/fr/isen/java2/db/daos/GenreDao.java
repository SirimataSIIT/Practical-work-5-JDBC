package fr.isen.java2.db.daos;

import java.util.List;
import fr.isen.java2.db.entities.Genre;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GenreDao {
	DataSource dataSource;
	Connection connection;

	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList();
		String prompt = "SELECT * FROM genre";

		try {
			dataSource = DataSourceFactory.getDataSource();
			connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(prompt);

			while(resultSet.next()) {
				genres.add(new Genre(resultSet.getInt(1), resultSet.getString(2)));
			}

			connection.close();
			return genres;
		} catch (SQLException e) {
			throw new RuntimeException("SQL Error at GenreDao.listGenre()");
		}

	}

	public Genre getGenre(String name) {
		String prompt = "SELECT * FROM genre WHERE name = '" + name + "'";

		try {
			dataSource = DataSourceFactory.getDataSource();
			connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(prompt);

			if (resultSet.next()) {
				Genre genre = new Genre(resultSet.getInt(1), resultSet.getString(2));
				connection.close();
				return genre;
			} else {
				connection.close();
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException("SQL Error at GenreDao.getGenre()");
		}
	}

	public void addGenre(String name) {
		String prompt = "INSERT INTO genre (name) VALUES (?)";

		try {
			dataSource = DataSourceFactory.getDataSource();
			connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(prompt);
			statement.setString(1, name);
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException("SQL Error at GenreDao.addGenre()");
		}
	}
}
