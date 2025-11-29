package prognet.network.server;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class RoomManager {

    private static final Logger LOGGER = Logger.getLogger(RoomManager.class.getName());
    private Map<String, Room> rooms;
    private Random random;

    public RoomManager() {
        this.rooms = new ConcurrentHashMap<>();
        this.random = new Random();
    }

    public synchronized String createRoom(String gridSize, String theme) {
        String roomCode = generateRoomCode();
        System.out.println("RoomManager: Generating room with code " + roomCode);
        Room room = new Room(roomCode, gridSize, theme);
        rooms.put(roomCode, room);
        LOGGER.info("Room created: " + roomCode);
        System.out.println("RoomManager: Room added to map, total rooms: " + rooms.size());
        return roomCode;
    }

    public Room getRoom(String roomCode) {
        return rooms.get(roomCode);
    }

    public synchronized void removeRoom(String roomCode) {
        Room room = rooms.remove(roomCode);
        if (room != null) {
            LOGGER.info("Room removed: " + roomCode);
        }
    }

    public synchronized void cleanupEmptyRooms() {
        rooms.entrySet().removeIf(entry -> {
            Room room = entry.getValue();
            if (room.isEmpty()) {
                LOGGER.info("Cleaning up empty room: " + entry.getKey());
                return true;
            }
            return false;
        });
    }

    private String generateRoomCode() {
        String code;
        do {
            code = String.format("%06d", random.nextInt(1000000));
        } while (rooms.containsKey(code));
        return code;
    }

    public int getRoomCount() {
        return rooms.size();
    }
}
