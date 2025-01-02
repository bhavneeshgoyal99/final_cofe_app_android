package com.cofe.solution.base;

import java.util.HashMap;
import java.util.Map;

public class ErrorMapping {

    // Map to hold error code and message pairs
    private static final Map<Integer, String> errorCodeMap = new HashMap<>();

    static {
        // Initialize the map with error codes and their corresponding messages
        errorCodeMap.put(-10000, "Invalid request");
        errorCodeMap.put(-10001, "SDK not initialized");
        errorCodeMap.put(-10002, "Invalid user parameter");
        errorCodeMap.put(-10003, "Invalid handle");
        errorCodeMap.put(-10004, "SDK cleanup error");
        errorCodeMap.put(-10005, "Timeout");
        errorCodeMap.put(-10006, "Memory error, failed to allocate memory");
        errorCodeMap.put(-10007, "Network error");
        errorCodeMap.put(-10008, "Failed to open file");
        errorCodeMap.put(-10009, "Unknown error");
        errorCodeMap.put(-11000, "Received data is incorrect, possibly version mismatch");
        errorCodeMap.put(-11001, "Version not supported");
        errorCodeMap.put(-11200, "Failed to open channel");
        errorCodeMap.put(-11201, "Failed to close channel");
        errorCodeMap.put(-11202, "Failed to establish media sub-connection");
        errorCodeMap.put(-11203, "Media sub-connection communication failed");
        errorCodeMap.put(-11204, "Maximum number of Nat video connections reached, no new Nat video connections allowed");
        errorCodeMap.put(-11300, "No permission");
        errorCodeMap.put(-11301, "Incorrect account password");
        errorCodeMap.put(-11302, "User does not exist");
        errorCodeMap.put(-11303, "User is locked");
        errorCodeMap.put(-11304, "User is not allowed to access (in blacklist)");
        errorCodeMap.put(-11305, "User has logged in");
        errorCodeMap.put(-11306, "User is not logged in");
        errorCodeMap.put(-11307, "Device may be offline");
        errorCodeMap.put(-11308, "Invalid user management input");
        errorCodeMap.put(-11309, "Duplicate index");
        errorCodeMap.put(-11310, "Object does not exist; used for querying");
        errorCodeMap.put(-11311, "Object does not exist");
        errorCodeMap.put(-11312, "Object is currently in use");
        errorCodeMap.put(-11313, "Subset exceeds range (e.g., group permissions exceed permission table, user permissions exceed group permission range, etc.)");
        errorCodeMap.put(-11314, "Incorrect password");
        errorCodeMap.put(-11315, "Passwords do not match");
        errorCodeMap.put(-11316, "Reserved account");
        errorCodeMap.put(-11400, "Application restart required after saving configuration");
        errorCodeMap.put(-11401, "System reboot required");
        errorCodeMap.put(-11402, "Error writing file");
        errorCodeMap.put(-11403, "Configuration feature not supported");
        errorCodeMap.put(-11404, "Configuration validation failed");
        errorCodeMap.put(-11405, "Requested or set configuration does not exist");
        errorCodeMap.put(-11500, "Pause failed");
        errorCodeMap.put(-11501, "Search failed, corresponding file not found");
        errorCodeMap.put(-11502, "Configuration not enabled");
        errorCodeMap.put(-11503, "Decoding failed");
        errorCodeMap.put(-11600, "Failed to create socket");
        errorCodeMap.put(-11601, "Failed to connect socket");
        errorCodeMap.put(-11602, "Domain name resolution failed");
        errorCodeMap.put(-11603, "Failed to send data");
        errorCodeMap.put(-11604, "Failed to obtain device information, device may not be online");
        errorCodeMap.put(-11605, "ARSP service busy");
        errorCodeMap.put(-11606, "ARSP service busy; select failed");
        errorCodeMap.put(-11607, "ARSP service busy; receive failed");
        errorCodeMap.put(-11608, "Failed to connect to server");
        errorCodeMap.put(-11609, "User does not exist");
        errorCodeMap.put(-11610, "Incorrect account password");
        errorCodeMap.put(-11611, "Query failed");
        errorCodeMap.put(-11612, "Server connection limit reached");
        errorCodeMap.put(-11700, "Device piracy");
        errorCodeMap.put(-200000, "Invalid parameter");
        errorCodeMap.put(-100001, "User does not exist");
        errorCodeMap.put(-100002, "SQL operation failed");
        errorCodeMap.put(-100003, "Incorrect password");
        errorCodeMap.put(-100004, "User does not have access to the device");
        errorCodeMap.put(-100005, "Login failed");
        errorCodeMap.put(-200004, "Device already exists, same as EE_CLOUD_DEV_MAC_EXSIT");
        errorCodeMap.put(-201110, "Search failed, corresponding file not found");
        errorCodeMap.put(-201118, "Failed to connect to the server");
        errorCodeMap.put(-210002, "Interface verification failed");
        errorCodeMap.put(-210003, "Parameter error");
        errorCodeMap.put(-210004, "Phone number has been registered");
        errorCodeMap.put(-210005, "Exceeded the daily limit of SMS sending (each number can only send registration verification messages three times a day)");
        errorCodeMap.put(-210010, "Sending failed");
        errorCodeMap.put(-210017, "Can only send once within 120 seconds");
        errorCodeMap.put(-210102, "Interface verification failed");
        errorCodeMap.put(-210103, "Parameter error");
        errorCodeMap.put(-210104, "Phone number has been registered");
        errorCodeMap.put(-210106, "Username has been registered");
        errorCodeMap.put(-210107, "Registration code verification error");
        errorCodeMap.put(-210110, "Registration failed (specific failure information in msg)");
        errorCodeMap.put(-210202, "Interface verification failed");
        errorCodeMap.put(-210203, "Parameter error");
        errorCodeMap.put(-210210, "Login failed");
        errorCodeMap.put(-210302, "Interface verification failed");
        errorCodeMap.put(-210303, "Parameter error");
        errorCodeMap.put(-210311, "New password does not meet the requirements");
        errorCodeMap.put(-210313, "Incorrect original password");
        errorCodeMap.put(-210315, "Please enter a new password different from the original password");
        errorCodeMap.put(-210402, "Interface verification failed");
        errorCodeMap.put(-210403, "Parameter error");

        // EE_AS_FORGET_PWD
        errorCodeMap.put(-210405, "Exceeded the daily limit of SMS sending (each number can only send registration verification messages three times a day)");
        errorCodeMap.put(-210410, "Sending failed (specific failure information in msg)");
        errorCodeMap.put(-210414, "Phone number does not exist");

        // EE_AS_RESET_PWD
        errorCodeMap.put(-210502, "Interface verification failed");
        errorCodeMap.put(-210503, "Parameter error");
        errorCodeMap.put(-210511, "New password does not meet the requirements");
        errorCodeMap.put(-210512, "The two entered new passwords do not match");
        errorCodeMap.put(-210514, "Phone number does not exist");

        // EE_AS_CHECK_PWD
        errorCodeMap.put(-210602, "Interface verification failed");
        errorCodeMap.put(-210603, "Parameter error");
        errorCodeMap.put(-210607, "Incorrect verification code");
        errorCodeMap.put(-210614, "Phone number does not exist");

        // EE_HTTP_PWBYEMAIL
        errorCodeMap.put(-213000, "No such username");
        errorCodeMap.put(-213001, "Sending failed");

        // EE_AS_SEND_EMAIL
        errorCodeMap.put(-213100, "Service response failed");
        errorCodeMap.put(-213102, "Interface verification failed");
        errorCodeMap.put(-213103, "Parameter error");
        errorCodeMap.put(-213108, "Email has been registered");
        errorCodeMap.put(-213110, "Sending failed");

        // EE_AS_REGISTE_BY_EMAIL
        errorCodeMap.put(-213200, "Service response failed");
        errorCodeMap.put(-213202, "Interface verification failed");
        errorCodeMap.put(-213203, "Parameter error");
        errorCodeMap.put(-213206, "Username has been registered");
        errorCodeMap.put(-213207, "Registration code verification error");
        errorCodeMap.put(-213208, "Email has been registered");
        errorCodeMap.put(-213210, "Registration failed");

        // EE_AS_SEND_EMAIL_FOR_CODE
        errorCodeMap.put(-213300, "Service response failed");
        errorCodeMap.put(-213302, "Interface verification failed");
        errorCodeMap.put(-213303, "Parameter error");
        errorCodeMap.put(-213310, "Sending failed");
        errorCodeMap.put(-213314, "Email does not exist");
        errorCodeMap.put(-213316, "Email and username do not match");

        // EE_CLOUD_DEV
        errorCodeMap.put(-213600, "In the blacklist (mac)");
        errorCodeMap.put(-213601, "Already exists");
        errorCodeMap.put(-213602, "MAC value is empty");
        errorCodeMap.put(-213603, "Invalid format (MAC address length is not 16 characters or contains keywords)");
        errorCodeMap.put(-213604, "Not in the whitelist");
        errorCodeMap.put(-213605, "Device name cannot be empty");
        errorCodeMap.put(-213606, "Incorrect device username format, contains keywords");
        errorCodeMap.put(-213607, "Incorrect device password format, contains keywords");
        errorCodeMap.put(-213608, "Incorrect device name format, contains keywords");

        // EE_CLOUD_SERVICE
        errorCodeMap.put(-213620, "Activation failed");
        errorCodeMap.put(-213621, "Cloud storage not available (1. User does not exist; 2. User has not activated)");
        errorCodeMap.put(-213630, "Authentication verification failed (incorrect username or password)");

        // EE_AS_REGISTER_EXTEND
        errorCodeMap.put(-214802, "Interface verification failed");
        errorCodeMap.put(-214803, "Parameter error");
        errorCodeMap.put(-214804, "Phone number has been registered");
        errorCodeMap.put(-214806, "Username has been registered");
        errorCodeMap.put(-214807, "Registration code verification error");
        errorCodeMap.put(-214810, "Registration failed (specific failure information in msg)");

        // EE_AS_REGISTE_BY_EMAIL_EXTEND
        errorCodeMap.put(-214900, "Service response failed");
        errorCodeMap.put(-214902, "Interface verification failed");
        errorCodeMap.put(-214903, "Parameter error");
        errorCodeMap.put(-214906, "Username has been registered");
        errorCodeMap.put(-214907, "Registration code verification error");
        errorCodeMap.put(-214908, "Email has been registered");
        errorCodeMap.put(-214910, "Registration failed");

        // EE_AS_SYS_NO_VALIDATED_REGISTER_EXTEND
        errorCodeMap.put(-215000, "Server response failed");
        errorCodeMap.put(-215002, "Interface verification failed");
        errorCodeMap.put(-215003, "Parameter error");
        errorCodeMap.put(-215006, "Username has been registered");
        errorCodeMap.put(-215010, "Registration failed");

        // EE_ACCOUNT_HTTP
        errorCodeMap.put(-604000, "Username or password error");
        errorCodeMap.put(-604010, "Incorrect verification code");
        errorCodeMap.put(-604011, "Passwords do not match");
        errorCodeMap.put(-604012, "Username has been registered");
        errorCodeMap.put(-604013, "Username is empty");
        errorCodeMap.put(-604014, "Password is empty");
        errorCodeMap.put(-604015, "Confirm password is empty");
        errorCodeMap.put(-604016, "Phone number is empty");
        errorCodeMap.put(-604017, "Incorrect username format");
        errorCodeMap.put(-604018, "Incorrect password format");
        errorCodeMap.put(-604019, "Incorrect confirm password format");
        errorCodeMap.put(-604020, "Incorrect phone number format");
        errorCodeMap.put(-604021, "Phone number already exists");
        errorCodeMap.put(-604022, "Phone number does not exist");
        errorCodeMap.put(-604023, "Email already exists");
        errorCodeMap.put(-604024, "Email does not exist");
        errorCodeMap.put(-604025, "User does not exist");
        errorCodeMap.put(-604026, "Incorrect original password");
        errorCodeMap.put(-604027, "Failed to modify password");
        errorCodeMap.put(-604029, "User ID is empty");
        errorCodeMap.put(-604030, "Verification code is empty");
        errorCodeMap.put(-604031, "Email is empty");
        errorCodeMap.put(-604032, "Incorrect email format");

        // EE_ACCOUNT_DEVICE
        errorCodeMap.put(-604100, "Illegal device, not allowed to add");
        errorCodeMap.put(-604101, "Device already exists");
        errorCodeMap.put(-604103, "Failed to modify device information");
        errorCodeMap.put(-604104, "Device UUID parameter exception");
        errorCodeMap.put(-604105, "Device username parameter exception");
        errorCodeMap.put(-604106, "Device password parameter exception");
        errorCodeMap.put(-604107, "Device port parameter exception");
        errorCodeMap.put(-604108, "Device extended field parameter exception (DEV_EXT_FAIL)");
        errorCodeMap.put(-604109, "");
        errorCodeMap.put(-604110, "New password verification failed");
        errorCodeMap.put(-604111, "Confirm password verification failed");
        errorCodeMap.put(-604112, "Device nickname verification failed");
        errorCodeMap.put(-604113, "");
        errorCodeMap.put(-604114, "Cloud storage supported");
        errorCodeMap.put(-604115, "Cloud storage not supported");
        errorCodeMap.put(-604116, "Failed to transfer device master account to another user. Check if the user has the device and the user has device master account permissions");
        errorCodeMap.put(-604117, "The current account is not the master account of the current device");
        errorCodeMap.put(-604118, "Device no longer exists, it has been removed");
        errorCodeMap.put(-604119, "Device addition is not unique, it has been added by another account");
        errorCodeMap.put(-604120, "Maximum limit reached for adding devices (100)");
        errorCodeMap.put(-604126, "Device supports token and can only be added by one account");
        errorCodeMap.put(-604127, "Device token is missing");
        errorCodeMap.put(-604129, "Device PID parameter exception (length greater than 60)");

        // EE_ACCOUNT_SEND_CODE
        errorCodeMap.put(-604300, "Failed to send");

        // EE_ACCOUNT_MESSAGE
        errorCodeMap.put(-604400, "SMS interface verification failed, please contact us");
        errorCodeMap.put(-604401, "SMS interface parameter error, please contact us");
        errorCodeMap.put(-604402, "SMS sending exceeded the limit, each phone number can only send three times a day");
        errorCodeMap.put(-604403, "Failed to send, please try again later");
        errorCodeMap.put(-604404, "Sending too frequently, please wait for 120 seconds between each send");

        // EE_ACCOUNT_HTTP
        errorCodeMap.put(-600900, "Server error");
        errorCodeMap.put(-605001, "Certificate does not exist");
        errorCodeMap.put(-605002, "Request header information error");
        errorCodeMap.put(-605003, "Certificate expired");
        errorCodeMap.put(-605004, "Encryption key verification error");
        errorCodeMap.put(-605005, "Parameter exception");
    }

    public static String getErrorMessage(int errorCode) {
        return errorCodeMap.getOrDefault(errorCode, "Unknown error code: " + errorCode);
    }


}