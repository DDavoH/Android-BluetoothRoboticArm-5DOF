package davoh.com.arduinobluetooth;

public class SingletonAddress {
        String address;
        private static final SingletonAddress ourInstance = new SingletonAddress();
        public static SingletonAddress getInstance() {
            return ourInstance;
        }
        private SingletonAddress() { }

        public void setAddress(String address) {
            this.address = address;
        }
        public String getAddress() {
            return address;
        }
}
