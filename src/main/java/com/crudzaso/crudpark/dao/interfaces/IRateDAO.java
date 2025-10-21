package com.crudzaso.crudpark.dao.interfaces;

import com.crudzaso.crudpark.model.Rate;
import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;

/**
 * Interface for Rate DAO operations
 */
public interface IRateDAO {

    /**
     * Finds an active rate by vehicle type
     * @param vehicleType type of vehicle (CAR or MOTORCYCLE)
     * @return Rate if found and active, null otherwise
     */
    Rate findActiveByVehicleType(VehicleTypeEnum vehicleType);

    /**
     * Finds a rate by ID
     * @param id rate's ID
     * @return Rate if found, null otherwise
     */
    Rate findById(int id);
}