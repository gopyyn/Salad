package com.salad.core;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.salad.utils.HibernateUtils.getSession;
import static java.util.Arrays.asList;

public class DatabaseContext {
    private static final int MAX_RESULT_COUNT = 10;

    private DatabaseContext() {
    }

    public static List<Map<String, Object>> execute(String query) {
        List<Map<String, Object>> results;
        if (StringUtils.startsWith(query.toLowerCase(), "select")) {
            results = executeSelectQuery(query);
        } else {
            int count = executeQuery(query);
            results = asList(new HashMap<String, Object>() {{ put("count", count); }});
        }
        SaladCommands.set("result", SaladCommands.convertToJsonString(results));
        return results;
    }

    @SuppressWarnings("unchecked")
    private static int executeQuery(String query) {
        Session session = getSession();
        try {
            Transaction transaction = session.beginTransaction();
            int count = session.createNativeQuery(query).executeUpdate();
            transaction.commit();
            return count;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    private static List<Map<String, Object>> executeSelectQuery(String query) {
        return getSession().createNativeQuery(query)
                .setMaxResults(MAX_RESULT_COUNT)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }
}