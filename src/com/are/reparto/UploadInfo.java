package com.are.reparto;

import com.csvreader.CsvReader;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadInfo {

    private static int contador=0;
    private static int cntFiles=0;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private static CharSequence string;
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String ruta = "C:\\REPARTO\\";
        File directorio = new File(ruta);
        Utilidades.AgregarLog("Descomprimiendo archivos ruta:" + ruta);
        DescomprimirArchivosCarpeta(directorio);
        Utilidades.AgregarLog("Procesando archivos de reparto en ruta:" + ruta);
        ProcesarDirectorio(directorio);
        System.out.println("Total archivos procesados: " + cntFiles);
        Utilidades.AgregarLog("Total archivos procesados: " + cntFiles);
        System.out.println("Total registros procesados: " + contador);
        Utilidades.AgregarLog("Total registros procesados: " + contador);

    }

    public static boolean GuardarRegistro(String[] fila, String filename) {

        return true;
    }

    public static void ProcesarDirectorio(File directorio) {
        System.out.println("Procesando Directorio: " + directorio.getPath());
        Utilidades.AgregarLog("Procesando Directorio: " + directorio.getPath());
        File[] ficheros = directorio.listFiles();
        for (File fichero : ficheros) {
            if (fichero.isDirectory()) {
                ProcesarDirectorio(fichero);
            } else {
                System.out.println("Validando nombre archivo: " + fichero.getPath());
                Utilidades.AgregarLog("Validando nombre archivo: " + fichero.getName());
                if (!getExtension(fichero.getName()).equals(".Z") && fichero.getPath().contains("INF_")) {
                    ProcesarArchivo(fichero.getPath());

                } else {
                    System.out.println("Archivo: " + fichero.getPath() + " DESCARTADO...");
                    Utilidades.AgregarLog("Archivo: " + fichero.getPath() + " DESCARTADO...");
                }

            }
        }

    }

    public static void ProcesarArchivo(String filename) {
        System.out.println("Procesando Archivo: " + filename );
        Utilidades.AgregarLog("Procesando Archivo: " + filename );
        java.util.Date femision= null;
        SimpleDateFormat fformat = new SimpleDateFormat("yyyyMMdd");
        File f = new File(filename);
        String nombreArchivo = f.getName();
        Pattern pattern = Pattern.compile("(\\d{8})");
        Matcher matcher = pattern.matcher(nombreArchivo);
        if (matcher.find()) {
            String fecha = matcher.group(1);
            Utilidades.AgregarLog("Fecha de emision encontrada." + fecha);
            try {
                femision = fformat.parse(fecha);
            } catch (ParseException ex) {
                 Utilidades.AgregarLog("Error al convertir la cadena a fecha: " + fecha);
            }
        }
        
        if (femision == null) {
            System.out.println("Fecha de emision no encontrada.");
            Utilidades.AgregarLog("Fecha de emision no encontrada.");
            return;
        }
        
        
        
        
        String cadena;
        CsvReader reader;
        db conexion = null;
        try {
            
           
            
            reader = new CsvReader(filename);
            reader.setDelimiter('|'); // tabulador
            reader.readHeaders();

            String[] headers = reader.getHeaders();
            if (headers.length != 19) { // estan las columnas OK.
                throw new IOException("Archivo no cargado.  Numero de columnas no coinciden con la estructura (col=18). Columnas archivo " + headers.length);
            }
            
            cntFiles++;
            conexion = new db();
            int registros=0;
            int cnt=0;
             while (reader.readRecord()) {
                 registros++;
                 String sql = "INSERT INTO tb_reparto (" +
                    " CLIENTE" +
                    ",TITULAR" +
                    ",DIRECCION" +
                    ",NIC" +
                    ",SIMBOLO_VARIABLE" +
                    ",NIF" +
                    ",NIS_RAD" +
                    ",UNICOM" +
                    ",RUTA" +
                    ",ITINERARIO" +
                    ",FLECT" +
                    ",TARIFA" +
                    ",DESC_TARIFA" +
                    ",IMPORTE" +
                    ",CSMO_KWH" +
                    ",IMPORTE_KWH" +
                    ",POSICION" +
                    ",SEC_ORDEN" +
                    ",FECHA_CARGA" +
                    ",USUARIO_CARGA" +
                    ",FILE_ORIGEN" +
                    ",CLASIFICACION" +
                    ",TIPOLOGIA" +
                    ",MIXTO" +
                    ",FECHA_CLASIFICACION" +
                    ",USUARIO_CLASIFICACION" +
                    ",DEVOLUCION" +
                    ",CONTRATO " +
                    ",F_EMISION)" +     
                    " VALUES (" +
                    "?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",?" +
                    ",SYSDATETIME()" +
                    ",'interfaz'" +
                    ",?" +
                    ",'-1'" +
                    ",'-1'" +
                    ",0" +
                    ",null" +
                    ",''" +
                    ",0" +
                    ",'-1'" +
                    ",? )";
                 java.sql.PreparedStatement pst = conexion.getConnection().prepareStatement(sql);
                 pst.setString(1, reader.get(0));
                 pst.setString(2, reader.get(1));
                 pst.setString(3, reader.get(2));
                 pst.setString(4, reader.get(3));
                 pst.setString(5, reader.get(4));
                 pst.setString(6, reader.get(5));
                 pst.setString(7, reader.get(6));
                 pst.setString(8, reader.get(7));
                 pst.setString(9, reader.get(8).replaceFirst ("^0*", ""));
                 pst.setString(10, reader.get(9).replaceFirst ("^0*", ""));
                 pst.setDate(11, new java.sql.Date(sdf.parse(reader.get(10)).getTime()) );
                 pst.setString(12, reader.get(11));
                 pst.setString(13, reader.get(12));
                 pst.setString(14, reader.get(13).replace(".", "").replace(",", "."));
                 pst.setString(15, reader.get(14).replace(".", "").replace(",", "."));
                 pst.setString(16, reader.get(15).replace(".", "").replace(",", "."));
                 pst.setString(17, reader.get(16).replace(".", "").replace(",", "."));
                 pst.setString(18, reader.get(17));
                 pst.setString(19, f.getName());
                 pst.setDate(20, new java.sql.Date(femision.getTime()));
                 try {
                    if (conexion.Update(pst) > 0) {
                        contador++;
                        cnt++;
                        conexion.Commit();
                    }
                 
                 }catch (SQLException ex) {
                     System.out.println("Error: " + ex.getMessage());
                    Utilidades.AgregarLog("Error: " + ex.getMessage());
                 }
                 
             }
             
            System.out.println("Total registros leidos del archivo " + filename + ": " + registros );
            Utilidades.AgregarLog("Total registros leidos del archivo " + filename + ": " + registros);
            System.out.println("Total registros cargados del archivo " + filename + ": " + cnt);
            Utilidades.AgregarLog("Total registros cargados del archivo " + filename + ": " + cnt);
            
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error: " + e.getMessage());
            Utilidades.AgregarLog("Error: " + e.getMessage());

        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            Utilidades.AgregarLog("Error: " + ex.getMessage());
        } catch (ParseException ex) {
           System.out.println("Error: " + ex.getMessage());
            Utilidades.AgregarLog("Error: " + ex.getMessage());
        } finally {
            if (conexion != null) {
                try {
                    conexion.Close();
                } catch (SQLException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    Utilidades.AgregarLog("Error: " + ex.getMessage());
                }
            }
        }

    }

    public static String getExtension(String name) {
        String extension = "";
        if (name.lastIndexOf('.') > 0) {
            // get last index for '.' char
            int lastIndex = name.lastIndexOf('.');
            // get extension
            extension = name.substring(lastIndex);
        }
        return extension;
    }

    public static void DescomprimirArchivosCarpeta(File directorio) {
        System.out.println("Procesando Directorio: " + directorio.getPath());
        Utilidades.AgregarLog("Procesando Directorio: " + directorio.getPath());
        File[] ficheros = directorio.listFiles();
        for (File fichero : ficheros) {
            if (fichero.isDirectory()) {
                DescomprimirArchivosCarpeta(fichero);
            } else {
                if (getExtension(fichero.getName()).equals(".Z") && fichero.getPath().contains("INF_")) {
                    // Procesa archivo no comprimido
                    System.out.println("Descomprimiendo archivo: " + fichero.getPath());
                    Utilidades.AgregarLog("Descomprimiendo archivo: " + fichero.getPath());
                    Utilidades.DescomprimirArchivo(fichero.getPath(), fichero.getParent());

                }
            }
        }
    }

}
