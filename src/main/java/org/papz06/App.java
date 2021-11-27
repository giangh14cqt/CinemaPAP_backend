package org.papz06;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.papz06.Function;

import java.io.Console;
import java.sql.*;
import org.papz06.Controllers.*;
import org.papz06.Models.Movie;

/**
 * Hello world!
 */
public class App {
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    public static void main(String[] args) {
        MovieCategoryController ciControl = new MovieCategoryController();
        ciControl.displayMovieCategoriesList();
    }
}
