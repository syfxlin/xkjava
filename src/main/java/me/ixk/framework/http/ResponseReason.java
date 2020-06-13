package me.ixk.framework.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseReason {
    private static final Map<Integer, String> reasonMap = new ConcurrentHashMap<>();

    static {
        reasonMap.put(100, "Continue");
        reasonMap.put(101, "Switching Protocols");
        reasonMap.put(102, "Processing");
        reasonMap.put(103, "Early Hints");
        reasonMap.put(200, "OK");
        reasonMap.put(201, "Created");
        reasonMap.put(202, "Accepted");
        reasonMap.put(203, "Non-Authoritative Information");
        reasonMap.put(204, "No Content");
        reasonMap.put(205, "Reset Content");
        reasonMap.put(206, "Partial Content");
        reasonMap.put(207, "Multi-Status");
        reasonMap.put(208, "Already Reported");
        reasonMap.put(209, "IM Used");
        reasonMap.put(300, "Multiple Choices");
        reasonMap.put(301, "Moved Permanently");
        reasonMap.put(302, "Found");
        reasonMap.put(303, "See Other");
        reasonMap.put(304, "Not Modified");
        reasonMap.put(305, "Use Proxy");
        reasonMap.put(306, "Switch Proxy");
        reasonMap.put(307, "Temporary Redirect");
        reasonMap.put(308, "Permanent Redirect");
        reasonMap.put(400, "Bad Request");
        reasonMap.put(401, "Unauthorized");
        reasonMap.put(402, "Payment Required");
        reasonMap.put(403, "Forbidden");
        reasonMap.put(404, "Not Found");
        reasonMap.put(405, "Method Not Allowed");
        reasonMap.put(406, "Not Acceptable");
        reasonMap.put(407, "Proxy Authentication Required");
        reasonMap.put(408, "Request Timeout");
        reasonMap.put(409, "Conflict");
        reasonMap.put(410, "Gone");
        reasonMap.put(411, "Length Required");
        reasonMap.put(412, "Precondition Failed");
        reasonMap.put(413, "Payload Too Large");
        reasonMap.put(414, "URI Too Long");
        reasonMap.put(415, "Unsupported Media Type");
        reasonMap.put(416, "Range Not Satisfiable");
        reasonMap.put(417, "Expectation Failed");
        reasonMap.put(418, "I'm a teapot");
        reasonMap.put(421, "Misdirected Request");
        reasonMap.put(422, "Unprocessable Entity");
        reasonMap.put(423, "Locked");
        reasonMap.put(424, "Failed Dependency");
        reasonMap.put(425, "Too Early");
        reasonMap.put(426, "Upgrade Required");
        reasonMap.put(428, "Precondition Required");
        reasonMap.put(429, "Too Many Requests");
        reasonMap.put(431, "Request Header Fields Too Large");
        reasonMap.put(444, "Connection Closed Without Response");
        reasonMap.put(451, "Unavailable For Legal Reasons");
        reasonMap.put(499, "Client Closed Request");
        reasonMap.put(500, "Internal Server Error");
        reasonMap.put(501, "Not Implemented");
        reasonMap.put(502, "Bad Gateway");
        reasonMap.put(503, "Service Unavailable");
        reasonMap.put(504, "Gateway Timeout");
        reasonMap.put(505, "HTTP Version Not Supported");
        reasonMap.put(506, "Variant Also Negotiates");
        reasonMap.put(507, "Insufficient Storage");
        reasonMap.put(508, "Loop Detected");
        reasonMap.put(510, "Not Extended");
        reasonMap.put(511, "Network Authentication Required");
        reasonMap.put(599, "Network Connect Timeout Error");
    }

    public static String getMessage(int code) {
        return reasonMap.get(code);
    }
}
