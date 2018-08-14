package com.example.minyoung.finding_dog;

import android.provider.BaseColumns;

public final class DataBases {
    public static final class CreateDB implements BaseColumns{
        public static final String SIDE = "side";
        public static final String TIME = "time";
        public static final String MSG = "msg";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +SIDE+" integer not null, "
                +TIME+" integer not null, "
                +MSG+" text not null);";

        public static final String getCreate0(String tableName){
            return "create table if not exists "+tableName+"("
                    +_ID+" integer primary key autoincrement, "
                    +SIDE+" integer not null, "
                    +TIME+" integer not null, "
                    +MSG+" text not null);";
        }
    }
}
