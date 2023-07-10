package com.foodo.models;

import com.foodo.GlobalData;
import com.foodo.utils.Utils;
import javafx.concurrent.Task;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * for restaurants and customers
 */
public class Address {

    private final String address;
    private final String[] splitComma;

    public Address(String address) {
        this.address = address;
        this.splitComma = address.split("\\s*,\\s*");
    }

    public Address(String... address) {

        this.splitComma = address;

        String[] addressCopy = Arrays.copyOf(address, address.length);

        for (int i = 1; i < address.length; i++) {

            addressCopy[i] = ", " + addressCopy[i];

        }

        this.address = Arrays.stream(addressCopy).reduce(String::concat).orElseThrow(() -> new NullPointerException("address must not be null"));

    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return address;
    }

    public Task<Void> exportToDb(Database db) {

        Task<Void> exporter = new Task<>() {
            @Override
            protected Void call() {

                try {

                    db.execute(String.format("INSERT INTO %s VALUES('%s')", GlobalData.Foodo.COVERED_ADDRESSES.T, address));
                    updateProgress(1, 1);

                } catch (Exception e) {

                    updateMessage(Utils.Logger.log(e));
                    updateProgress(0, 1);

                }

                return null;
            }
        };

        GlobalData.globalNonDaemonExecutor.submit(exporter);

        return exporter;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address1 = (Address) o;
        return Objects.equals(address, address1.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
