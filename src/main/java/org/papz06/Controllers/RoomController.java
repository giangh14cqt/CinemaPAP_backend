package org.papz06.Controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.papz06.Function;
import org.papz06.Models.Room;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoomController {

    public static JSONArray getRoomListByCinema(int id) {
        /*
        Get list of rooms in cinema
         */
        JSONArray resultData = new JSONArray();
        Function fc = new Function();
        ResultSet rs;
        try {
            String sqlSelect = String.format("select room_id, name from rooms where cinema_id = %d and available = 1", id);
            rs = fc.executeQuery(sqlSelect);
            while (rs.next()) {
                JSONObject roomData = new JSONObject();
                roomData.put("id", rs.getInt(1));
                roomData.put("name", rs.getString(2));
                resultData.put(roomData);
            }
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return resultData;
    }

    public static JSONObject getRoomWithSeatsById(Integer id, boolean withSeats) {
        /*
        Get room id details with or without seats for the room
         */
        JSONObject roomData = new JSONObject();
        Function fc = new Function();
        ResultSet rs;
        try {
            String sqlSelect = String.format("select room_id, name from rooms where room_id = %d and available = 1", id);
            rs = fc.executeQuery(sqlSelect);
            while (rs.next()) {
                roomData.put("id", rs.getInt(1));
                roomData.put("name", rs.getString(2));
            }
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        if (withSeats) {
            roomData.put("seats", SeatController.getSeatListByRoomId(id));
        }
        return roomData;
    }

    public static Room getRoomById(Integer id) {
        /*
        Get room data by id
         */
        Room roomData = new Room();
        Function fc = new Function();
        ResultSet rs;
        try {
            String sqlSelect = String.format("select * from rooms where room_id = %d and available = 1", id);
            rs = fc.executeQuery(sqlSelect);
            while (rs.next()) {
                roomData = new Room(rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5));
            }
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return roomData;
    }

    public static JSONObject getRoomWithCinemaById(int id) {
        /*
        Room details by id
         */
        JSONObject roomData = new JSONObject();
        Function fc = new Function();
        ResultSet rs;
        try {
            String sqlSelect = String.format("select room_id, name, cinema_id from rooms where room_id = %d and available = 1", id);
            rs = fc.executeQuery(sqlSelect);
            while (rs.next()) {
                roomData.put("id", rs.getInt(1));
                roomData.put("name", rs.getString(2));
                roomData.put("cinema_id", rs.getInt(3));
            }
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return roomData;
    }

    public static JSONObject insertNewRoom(String newRoomName, int rowsNumber, int seatsInRowNumber, int newCinemaId) {
        /*
        Create new room
         */
        Function fc = new Function();
        ResultSet rs;
        int roomId = 0;
        try {
            String sqlInsert = String.format("insert into rooms values (default, '%s', %d, %d, %d, default)",
                    newRoomName, rowsNumber, seatsInRowNumber, newCinemaId);
            fc.executeQuery(sqlInsert);
            rs = fc.executeQuery("select * from rooms where available = 1 order by room_id desc fetch next 1 rows only");
            rs.next();
            roomId = rs.getInt(1);
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        // data about new room
        return getRoomWithCinemaById(roomId);
    }

    public static JSONObject updateRoomData(Integer id, String newName, int rowsNumber, int seatsInRowNumber) {
        /*
        Update existing room data
         */
        Function fc = new Function();
        try {
            String sqlUpdate = String.format(
                    "update rooms set name = '%s', rowsNumber = %d, seatsInRowNumber = %d where room_id = %d and available = 1",
                    newName, rowsNumber, seatsInRowNumber, id);
            fc.executeQuery(sqlUpdate);
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return getRoomWithSeatsById(id, false);
    }

    public static JSONObject deleteRoom(Integer id) {
        /*
        Delete room by setting available to 0
         */
        Function fc = new Function();
        try {
            String sqlDelete = String.format("update rooms set available = 0 where room_id = %d", id);
            fc.executeQuery(sqlDelete);
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return new JSONObject();
    }

    public static List<Room> getRoomList() {
        /*
        Get list of room objects
         */
        List<Room> roomsList = new ArrayList<>();
        Function fc = new Function();
        ResultSet rs;
        try {
            rs = fc.executeQuery("select * from rooms where available = 1");
            while (rs.next()) {
                roomsList.add(
                        new Room(rs.getInt(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getInt(4),
                                rs.getInt(5)
                        )
                );
            }
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println(e);
        }
        return roomsList;
    }

    public static boolean isNameEmpty(String newName) {
        // check whether string is empty
        return newName.length() == 0;
    }

    public static boolean checkExist(String name, int cinemaId) {
        // check whether room exists
        for (Room rm : getRoomList()) {
            if (rm.getCinemaId().equals(cinemaId))
                if (rm.getName().equals(name))
                    return true;
        }
        return false;
    }

    public static boolean checkExist(Integer id) {
        // check whether room exists
        for (Room rm : getRoomList()) {
            if (rm.getId().equals(id))
                return true;
        }
        return false;
    }
}
