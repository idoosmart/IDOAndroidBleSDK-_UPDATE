package test.com.ido.runplan;

public class RunPlanToggleH5Info {

    /**
     * message : {"operate":1,"type":"1","year":"2022","month":"05","day":"0","hour":12,"minute":24,"second":50}
     */

    private MessageBean message;

    public MessageBean getMessage() {
        return message;
    }

    public void setMessage(MessageBean message) {
        this.message = message;
    }

    public static class MessageBean {
        /**
         * operate : 1
         * type : 1
         * year : 2022
         * month : 05
         * day : 0
         * hour : 12
         * minute : 24
         * second : 50
         */
        private int operate;
        private int training_offset;
        private String type;
        private String year;
        private String month;
        private String day;
        private int hour;
        private int minute;
        private int second;

        public int getTraining_offset() {
            return training_offset;
        }

        public void setTraining_offset(int training_offset) {
            this.training_offset = training_offset;
        }

        public int getOperate() {
            return operate;
        }

        public void setOperate(int operate) {
            this.operate = operate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }
    }
}
