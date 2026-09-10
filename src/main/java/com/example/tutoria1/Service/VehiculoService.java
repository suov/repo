package com.example.tutoria1.Service;

import com.example.tutoria1.Model.Vehiculo.Vehiculo;
import java.util.List;

public interface VehiculoService {

    Vehiculo crearVehiculo(Vehiculo vehiculo);

    List<Vehiculo> listarVehiculos();

    Vehiculo obtenerVehiculoPorId(Long id);

    Vehiculo actualizarVehiculo(Long id, Vehiculo vehiculo);    

    void eliminarVehiculo(Long id);
}