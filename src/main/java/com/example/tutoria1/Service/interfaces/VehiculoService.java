package com.example.tutoria1.Service;

import java.util.List;

import com.example.tutoria1.Model.VehiculoModel;

public interface VehiculoService {

    VehiculoModel crearVehiculo(VehiculoModel vehiculo);

    List<VehiculoModel> listarVehiculos();

    VehiculoModel obtenerVehiculoPorId(Long id);

    VehiculoModel actualizarVehiculo(Long id, VehiculoModel vehiculo);

    void eliminarVehiculo(Long id);
}