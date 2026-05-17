package com.vamshi.stockflow_backend.warehouse.service;

import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import com.vamshi.stockflow_backend.warehouse.dto.NearestWarehouseResponse;
import com.vamshi.stockflow_backend.warehouse.mapper.WarehouseMapper;
import com.vamshi.stockflow_backend.warehouse.repository.WarehouseRepository;
import com.vamshi.stockflow_backend.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Test
    void findNearestWarehouses_ShouldReturnWarehousesWithinServiceRadius() {
        Warehouse warehouse = Warehouse.builder()
                .name("Hyderabad Warehouse")
                .city("Hyderabad")
                .pincode("500081")
                .address("Madhapur")
                .latitude(17.4483)
                .longitude(78.3915)
                .serviceRadiusKm(20.0)
                .active(true)
                .build();

        warehouse.setId(UUID.randomUUID());

        when(warehouseRepository.findByActiveTrue()).thenReturn(List.of(warehouse));

        List<NearestWarehouseResponse> result =
                warehouseService.findNearestWarehouses(17.3850, 78.4867);

        assertEquals(1, result.size());
        assertEquals("Hyderabad Warehouse", result.get(0).getName());
        assertTrue(result.get(0).getDistanceKm() <= 20.0);

        verify(warehouseRepository, times(1)).findByActiveTrue();
    }

    @Test
    void findNearestWarehouses_ShouldIgnoreWarehousesOutsideServiceRadius() {
        Warehouse warehouse = Warehouse.builder()
                .name("Far Warehouse")
                .city("Hyderabad")
                .pincode("500081")
                .address("Far Area")
                .latitude(17.4483)
                .longitude(78.3915)
                .serviceRadiusKm(1.0)
                .active(true)
                .build();

        warehouse.setId(UUID.randomUUID());

        when(warehouseRepository.findByActiveTrue()).thenReturn(List.of(warehouse));

        List<NearestWarehouseResponse> result =
                warehouseService.findNearestWarehouses(17.3850, 78.4867);

        assertTrue(result.isEmpty());

        verify(warehouseRepository, times(1)).findByActiveTrue();
    }

    @Test
    void findNearestWarehouses_ShouldIgnoreWarehousesWithoutLatLng() {
        Warehouse warehouse = Warehouse.builder()
                .name("No Location Warehouse")
                .city("Hyderabad")
                .pincode("500081")
                .address("Unknown")
                .latitude(null)
                .longitude(null)
                .serviceRadiusKm(10.0)
                .active(true)
                .build();

        warehouse.setId(UUID.randomUUID());

        when(warehouseRepository.findByActiveTrue()).thenReturn(List.of(warehouse));

        List<NearestWarehouseResponse> result =
                warehouseService.findNearestWarehouses(17.3850, 78.4867);

        assertTrue(result.isEmpty());

        verify(warehouseRepository, times(1)).findByActiveTrue();
    }
}