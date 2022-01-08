package org.papz06.Models;

import java.util.Date;
import org.json.JSONObject;
import org.papz06.Controllers.MovieController;
import org.papz06.Controllers.RoomController;

public class Schedule {
    int id, seatLeft, filmId, roomId;
    Date datetime, openSale, closeSale;
    Movie film;
    Room room;

    public Schedule(int id, int seatLeft, Date datetime, Date openSale, Date closeSale, Movie film, Room room) {
        this.id = id;
        this.datetime = datetime;
        this.film = film;
        this.room = room;
        this.openSale = openSale;
        this.closeSale = closeSale;
        this.seatLeft = seatLeft;
        this.filmId = film.getId();
        this.roomId = room.getId();
    }

    public Schedule(Date datetime, Date openSale, Date closeSale, int filmId, int roomId) {
        this.datetime = datetime;
        this.filmId = filmId;
        this.roomId = roomId;
        this.openSale = openSale;
        this.closeSale = closeSale;
        this.room = RoomController.getRoomById(roomId);
        this.film = MovieController.getMovieById(filmId);
        this.seatLeft = this.room.getSeatNumber();
    }

    public Schedule() {
    }

    public JSONObject toJsonDetail() {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("datetime", datetime);
        result.put("film", film.toJson());
        result.put("room", room.toJson());
        result.put("openSale", openSale);
        result.put("closeSale", closeSale);
        result.put("seatLeft", seatLeft);
        return result;
    }

    public JSONObject toJsonGeneral() {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("datetime", datetime);
        result.put("filmId", filmId);
        result.put("roomId", roomId);
        result.put("openSale", openSale);
        result.put("closeSale", closeSale);
        result.put("seatLeft", seatLeft);
        return result;
    }

    @Override
    public String toString() {
        return id + " " + datetime
                + " " + film.toString()
                + " " + room.toString()
                + " " + openSale
                + " " + closeSale
                + " " + seatLeft;
    }
}
