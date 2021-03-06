package org.papz06.Models;

import org.json.JSONObject;
import org.papz06.Controllers.SeatController;

public class Room {
    int id, rowsNumber, seatsInRowNumber, cinemaId;
    String name;

    public Room(int id, String name, int rowsNumber, int seatsInRowNumber, int cinemaId) {
        this.id = id;
        this.name = name;
        this.rowsNumber = rowsNumber;
        this.seatsInRowNumber = seatsInRowNumber;
        this.cinemaId = cinemaId;
    }

    public int getSeatNumber() {
        return rowsNumber * seatsInRowNumber;
    }

    public Room() {
    }

    @Override
    public String toString() {
        return id + " " + rowsNumber
                + " " + seatsInRowNumber
                + " " + cinemaId
                + " " + name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRowsNumber() {
        return rowsNumber;
    }

    public int getSeatsInRowNumber(){
        return seatsInRowNumber;
    }

    public Integer getCinemaId() {
        return cinemaId;
    }

    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("name", name);
        result.put("rowsNumber", rowsNumber);
        result.put("seatsInRowNumber", seatsInRowNumber);
        result.put("cinemaId", cinemaId);
        return result;
    }

    public JSONObject toJsonDetails(int sch_id) {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("name", name);
        result.put("rowsNumber", rowsNumber);
        result.put("seatsInRowNumber", seatsInRowNumber);
        result.put("cinemaId", cinemaId);
        result.put("seats", SeatController.getSeatsListBySchedule(sch_id));
        return result;
    }
}
