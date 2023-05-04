package net.revature.ecommerce.exceptions;

import java.util.Date;

public class ErrorMessage {
    Date date;
    String message;
    ErrorMessage(Date date, String message) {
        this.date = date;
        this.message = message;
    }
}
