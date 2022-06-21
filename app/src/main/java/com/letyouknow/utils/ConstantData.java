package com.letyouknow.utils;

public class ConstantData {
    String formatPhoneNo(String phon) {
        String mNo = "(" + phon;
        String mno1 = insertString(mNo, ")", 3);
        String mno2 = insertString(mno1, "-", 7);
        return mno2;
    }

    String insertString(
            String originalString,
            String stringToBeInserted,
            int index
    ) {

        // Create a new string

        // return the modified String
        try {
            return (originalString.substring(0, index + 1)
                    + stringToBeInserted
                    + originalString.substring(index + 1));
        } catch (Exception e) {

        }
        return "";
    }
}
