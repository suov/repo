package com.example.tutoria1.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.tutoria1.Service.interfaces.IExcelService;
import com.example.tutoria1.repository.TrayectoRepository;

@Service("ExcelService")
public class ExcelService implements IExcelService {

    // ========== INYECCIÓN DE DEPENDENCIAS ==========

    @Autowired
    @Qualifier("ITrayectoRepository")
    private TrayectoRepository ITrayectoRepository;

    // ========== LOGS ==========

    private static final Logger logger =
            org.apache.logging.log4j.LogManager.getLogger(ExcelService.class);

    // ========== PROCESAMIENTO DEL ARCHIVO EXCEL ==========

    public void processExcel(MultipartFile file, String loginUsuario) {

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            LocalDateTime now = LocalDateTime.now();

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyyMMddHHmm");

            String formattedDateTime = now.format(formatter);

            Double idCargue = Double.parseDouble(formattedDateTime);

            DataFormatter dataFormatter = new DataFormatter();

            for (Row row : sheet) {

                // Omitir la primera fila, que contiene los encabezados.
                if (row.getRowNum() > 0) {

                    String personaId = dataFormatter.formatCellValue(
                            row.getCell(0)).trim();

                    String vehiculoId = dataFormatter.formatCellValue(
                            row.getCell(1)).trim();

                    String codigoRuta = dataFormatter.formatCellValue(
                            row.getCell(2)).trim();

                    String ubicacion = dataFormatter.formatCellValue(
                            row.getCell(3)).trim();

                    String ordenParada = dataFormatter.formatCellValue(
                            row.getCell(4)).trim();

                    System.out.println(
                            "Dato: " + personaId
                            + " - " + vehiculoId
                            + " - " + codigoRuta
                            + " - " + ubicacion
                            + " - " + ordenParada
                            + " - " + idCargue
                    );

                    if (this.cargarDatosExcel(
                            personaId,
                            vehiculoId,
                            codigoRuta,
                            ubicacion,
                            ordenParada,
                            null,
                            null,
                            loginUsuario,
                            idCargue)) {

                        System.out.println(
                                "Cargue de trayecto inicial. EXITOSO!!!!!");
                    }
                }
            }

            if (this.validarDatosExcel(idCargue)) {

                System.out.println(
                        "Validación de datos cargados. EXITOSO!!!!!");

                if (this.procesarDatosExcel(idCargue)) {

                    System.out.println(
                            "Procesado de datos validados. EXITOSO!!!!!");
                }
            }

        } catch (Exception e) {

            logger.error(
                    "ERROR PROCESANDO EL ARCHIVO EXCEL DE TRAYECTOS", e);
        }
    }

    // ========== CARGAR DATOS EN LA TABLA TEMPORAL ==========

    @Override
    public boolean cargarDatosExcel(
            String personaId,
            String vehiculoId,
            String codigoRuta,
            String ubicacion,
            String ordenParada,
            String latitud,
            String longitud,
            String loginUsuario,
            Double idCargue) {

        try {

            ITrayectoRepository.CARGAR_TRAYECTOS_EXCEL(
                    personaId,
                    vehiculoId,
                    codigoRuta,
                    ubicacion,
                    ordenParada,
                    latitud,
                    longitud,
                    loginUsuario,
                    idCargue);

            logger.info(
                    "Se carga registro de Excel a tabla temporal exitosamente.");

            return true;

        } catch (Exception e) {

            logger.error(
                    "ERROR CARGANDO DATOS DEL EXCEL DE TRAYECTOS", e);

            return false;
        }
    }

    // ========== VALIDAR DATOS ==========

    @Override
    public boolean validarDatosExcel(Double idCargue) {

        try {

            ITrayectoRepository.VALIDAR_TRAYECTOS_CARGUE(idCargue);

            logger.info(
                    "Se validan los registros del cargue exitosamente.");

            return true;

        } catch (Exception e) {

            logger.error(
                    "ERROR VALIDANDO LOS DATOS DEL EXCEL DE TRAYECTOS", e);

            return false;
        }
    }

    // ========== PROCESAR DATOS ==========

    @Override
    public boolean procesarDatosExcel(Double idCargue) {

        try {

            ITrayectoRepository.PROCESAR_TRAYECTOS_EXCEL(idCargue);

            logger.info(
                    "Se procesan los trayectos validados exitosamente.");

            return true;

        } catch (Exception e) {

            logger.error(
                    "ERROR PROCESANDO LOS DATOS DEL EXCEL DE TRAYECTOS", e);

            return false;
        }
    }
}