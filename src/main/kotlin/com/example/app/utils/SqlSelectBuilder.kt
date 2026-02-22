package com.example.app.utils



class SqlSelectBuilder {
    private var sqlWith: ArrayList<String>? = null
    private var sqlSelect = ArrayList<String>()
    private var sqlFrom = ArrayList<String>()
    private var sqlJoin: ArrayList<String>? = null
    private var sqlWhere: String? = null
    private var sqlGroupBy: String? = null
    private var sqlHaving: String? = null
    private var sqlOrderBy: String? = null
    var paramName: HashMap<String, Any>? = null
    private var sqlScript: String = ""

    fun with(with: () -> ArrayList<String>?): SqlSelectBuilder {
        with()?.let {
            sqlWith = it
        }
        return this
    }


    fun select(fields: () -> ArrayList<String>): SqlSelectBuilder {
        sqlSelect.addAll(fields())
        return this
    }


    fun from(f: () -> ArrayList<String>): SqlSelectBuilder {
        sqlSelect.addAll(f())
        return this
    }


    fun join(j: () -> ArrayList<String>?): SqlSelectBuilder {
        j()?.let { sqlJoin = it }
        return this
    }


    fun where(w: () -> String): SqlSelectBuilder {
        sqlWhere = w()
        return this
    }


    fun groupBy(group: () -> String): SqlSelectBuilder {
        sqlGroupBy = group()
        return this
    }


    fun having(have: () -> String): SqlSelectBuilder {
        sqlHaving = have()
        return this
    }


    fun orderBy(order: () -> String): SqlSelectBuilder {
        sqlOrderBy = order()
        return this
    }


    fun script(scr: () -> String): SqlSelectBuilder {
        sqlScript = scr()
        return this
    }


    fun parameterName(param: () -> HashMap<String, Any>?): SqlSelectBuilder {
        param()?.let { this.paramName = it }
        return this
    }


    fun build(): SqlSelect {
        if (sqlScript.isNotBlank()) return SqlSelect(sqlScript, paramName)

        return SqlSelect(sqlWith, sqlSelect, sqlFrom,sqlJoin, sqlWhere,
            sqlGroupBy, sqlHaving, sqlOrderBy, paramName)
    }
}



class SqlSelect {
    var paramName: HashMap<String, Any>? = null
    var sqlQuery = StringBuilder()

    constructor(script: String = "", param: HashMap<String, Any>? = null) {
        sqlQuery.append(script)
        this.paramName = param
    }

    constructor(sqlWith: ArrayList<String>? = null, sqlSelect: ArrayList<String>,
                sqlFrom: ArrayList<String>, sqlJoin: ArrayList<String>? = null,
                sqlWhere: String? = null, sqlGroupBy: String? = null,
                sqlHaving: String? = null, sqlOrderBy: String? = null,
                paramName: HashMap<String, Any>? = null) {
        this.paramName = paramName
        sqlWith?.let {
            sqlQuery.append("with")
            sqlQuery.append(" ")
            sqlQuery.append(it.joinToString(",\n"))
            sqlQuery.append(" ")
        }

        sqlWhere?.let {
            sqlQuery.append("\n\t")
            sqlQuery.append("group by ")
            sqlQuery.append(it)
        }

        sqlHaving?.let {
            sqlQuery.append(" ")
            sqlQuery.append("having ")
            sqlQuery.append(it)
        }

        sqlOrderBy?.let {
            sqlQuery.append(" ")
            sqlQuery.append("order by ")
            sqlQuery.append(it)
        }
    }

    override fun toString(): String {
        return sqlQuery.toString()
    }
}
