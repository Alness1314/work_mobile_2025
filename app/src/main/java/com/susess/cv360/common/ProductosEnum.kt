package com.susess.cv360.common

enum class ProductosEnum(
    val clave: String,
    val descripcion: String,
    val claveSubproducto: String,
    val descripSubProducto: String,
    val unidadMedida: String
) {
    SP18("PR03", "Diésel", "SP18", "Diésel automotriz", "UM03"),
    SP19("PR03", "Diésel", "SP19", "Diésel marino", "UM03"),
    SP22("PR03", "Diésel", "SP22", "IFO380", "UM03"),
    SP23("PR03", "Diésel", "SP23", "Diésel industrial", "UM03"),
    SP24("PR03", "Diésel", "SP24", "Diésel de Ultra Bajo Azufre (DUBA)", "UM03"),
    SP25("PR03", "Diésel", "SP25", "Diésel agrícola", "UM03"),

    SP16("PR07", "Gasolina", "SP16", "Gasolina regular menor a 91 octanos", "UM03"),
    SP17("PR07", "Gasolina", "SP17", "Gasolina premium mayor o igual a 91 octanos", "UM03"),

    SP1("PR08", "Petróleo", "SP1", "Súper- ligero/Dulce", "UM01"),
    SP2("PR08", "Petróleo", "SP2", "Súper-ligero/Semi-amargo", "UM01"),
    SP3("PR08", "Petróleo", "SP3", "Súper-ligero/Amargo", "UM01"),
    SP4("PR08", "Petróleo", "SP4", "Ligero/Dulce", "UM01"),
    SP5("PR08", "Petróleo", "SP5", "Ligero/Semi-amargo", "UM01"),
    SP6("PR08", "Petróleo", "SP6", "Ligero/Amargo", "UM01"),
    SP7("PR08", "Petróleo", "SP7", "Mediano/Dulce", "UM01"),
    SP8("PR08", "Petróleo", "SP8", "Mediano/Semi-amargo", "UM01"),
    SP9("PR08", "Petróleo", "SP9", "Mediano/Amargo", "UM01"),
    SP10("PR08", "Petróleo", "SP10", "Pesado/Dulce", "UM01"),
    SP11("PR08", "Petróleo", "SP11", "Pesado/Semi-amargo", "UM01"),
    SP12("PR08", "Petróleo", "SP12", "Pesado/Amargo", "UM01"),
    SP13("PR08", "Petróleo", "SP13", "Extra-pesado/Dulce", "UM01"),
    SP14("PR08", "Petróleo", "SP14", "Extra-pesado/Semi-amargo", "UM01"),
    SP15("PR08", "Petróleo", "SP15", "Extra-pesado/Amargo", "UM01"),

    SP27("PR09", "Gas Natural", "SP27", "Gas natural vehicular comprimido", "UM04"),
    SP28("PR09", "Gas Natural", "SP28", "Gas natural comprimido", "UM04"),
    SP29("PR09", "Gas Natural", "SP29", "Gas natural licuado", "UM04"),
    SP37("PR09", "Gas Natural", "SP37", "Líquidos del gas natural", "UM04"),
    SP38("PR09", "Gas Natural", "SP38", "Gas natural sin procesar", "UM02"),
    SP41("PR09", "Gas Natural", "SP41", "Gas natural procesado", "UM04"),
    SP42("PR09", "Gas Natural", "SP42", "Metano", "UM04"),
    SP43("PR09", "Gas Natural", "SP43", "Butano", "UM04"),
    SP44("PR09", "Gas Natural", "SP44", "Etano", "UM04"),
    SP47("PR09", "Gas Natural", "SP47", "Gasolina natural", "UM04"),

    SP34("PR09", "Turbosina", "SP34", "JET-A", "UM03"),
    SP35("PR09", "Turbosina", "SP35", "JET-A1", "UM03"),

    PR12("PR12", "Gas L.P.", "", "", "UM03"),

    SP30("PR13", "Combustóleo", "SP30", "Combustóleo menor o igual a 1% de azufre", "UM03"),
    SP31("PR13", "Combustóleo", "SP31", "Combustóleo mayor a 1% y menor a 3% de azufre", "UM03"),
    SP32(
        "PR13",
        "Combustóleo",
        "SP32",
        "Combustóleo mayor o igual a 3% y menor a 4.4% de azufre",
        "UM03"
    ),
    SP33("PR13", "Combustóleo", "SP33", "IFO 180", "UM03"),

    SP20("PR15", "Bioenergético", "SP20", "Otros", "UM03"),
    SP21("PR15", "Bioenergético", "SP21", "Biodiesel", "UM03"),
    SP36("PR15", "Bioenergético", "SP36", "Bioturbosina", "UM03"),
    SP39(
        "PR15",
        "Bioenergético",
        "SP39",
        "Etanol anhidro contenido máximo de 10 % como oxigenante en gasolinas Regular",
        "UM03"
    ),
    SP40(
        "PR15",
        "Bioenergético",
        "SP40",
        "Etanol anhidro contenido máximo de 10 % como oxigenante en gasolinas Premium",
        "UM03"
    ),

    SP48("PR16", "Gasóleo", "SP48", "Gasóleo doméstico", "UM03"),

    SP45("PR17", "Naftas", "SP45", "Naftas pesadas", "UM03"),
    SP46("PR17", "Naftas", "SP46", "Naftas ligeras", "UM03"),

    SP26("PR18", "Gasavión", "SP26", "Gasavión", "UM03"),

    PR19("PR19", "Hidratos de metano", "SP42", "Metano", "UM02"),

    PR10("PR10", "Condensado", "", "", "UM01"),

    PR14("PR14", "Propano", "", "", "UM03"),

    PR20("PR20", "Otros", "SP20", "Otros", "UM03"),
    SP49("PR20", "Otros", "SP49", "MTBE", "UM03");

    override fun toString(): String {
        return descripcion
    }

    companion object {
        fun obtenerProductoPorClaveSubproducto(claveSubproducto: String): ProductosEnum? {
            return values().find { it.claveSubproducto == claveSubproducto }
        }

        fun obtenerProductoPorClave(claveProducto: String): ProductosEnum? {
            return values().find { it.clave == claveProducto }
        }

        fun obtenerTodosLosProductos(): List<ProductosEnum> {
            return values().toList()
        }
    }
}