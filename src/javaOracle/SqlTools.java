package javaOracle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import oracle.jdbc.OracleCallableStatement;

public class SqlTools {
	
	// El siguiente metodo prepara una llamada a procedimiento. Por ejemplo la llamada
	//    ConstruirLlamadaProcedimiento("prueba","MOSTRAREQUIPO",2)
	// genera "{call prueba.MOSTRAREQUIPO (?,?)}"
	public static String ConstruirLlamadaProcedimiento(String packageName, String procedureName, int cantidadParametros){
		StringBuffer sb = new StringBuffer("{call "+packageName+"."+procedureName+"(");
		for(int i=1;i<=cantidadParametros;i++){
			sb.append("?");
			if(i<cantidadParametros) sb.append(",");
		}
		return sb.append(")}").toString();
	}
	public static String ConstruirLlamadaProcedimiento(String procedureName, int cantidadParametros){
		StringBuffer sb = new StringBuffer("{call "+procedureName+"(");
		for(int i=1;i<=cantidadParametros;i++){
			sb.append("?");
			if(i<cantidadParametros) sb.append(",");
		}
		return sb.append(")}").toString();
	}
	
	public static String ConstruirLlamadaFuncion(String procedureName, int cantidadParametros){
		StringBuffer sb = new StringBuffer("{ ? = call"+procedureName+"(");;
		for(int i=1;i<=cantidadParametros;i++){
			sb.append("?");
			if(i<cantidadParametros) sb.append(",");
		}
		return sb.append(")}").toString();
	}
	
	// El siguiente metodo cierra los recursos abiertos (ResultSet ,Statement ,OracleCallableStatement, Connection)
	public static void close(ResultSet rs, Statement s,OracleCallableStatement os, Connection c){
		try { if(rs!=null) rs.close();}catch(Exception e){e.printStackTrace();};
		try { if(s!=null) s.close();}catch(Exception e){e.printStackTrace();};
		try { if(os!=null) os.close();}catch(Exception e){e.printStackTrace();};		
		try { if(c!=null) c.close();}catch(Exception e){e.printStackTrace();};		
	}
}
