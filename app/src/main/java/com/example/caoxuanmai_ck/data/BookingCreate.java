package com.example.caoxuanmai_ck.data;
import java.util.*;
public class BookingCreate {
    public long showtimeId;
    public List<String> seatCodes;
    public BookingCreate(long id, java.util.List<String> codes){ showtimeId=id; seatCodes=codes; }
}