package br.com.rtools.utilitarios;

import br.com.rtools.principal.DB;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class Dao extends DB implements DaoInterface {

    /**
     * <p>
     * <strong>Open Transaction</strong></p>
     *
     * @author Bruno
     */
    @Override
    public void openTransaction() {
        getEntityManager().getTransaction().begin();
    }

    /**
     * <p>
     * <strong>Commit</strong></p>
     *
     * @author Bruno
     */
    @Override
    public void commit() {
        getEntityManager().getTransaction().commit();
    }

    /**
     * <p>
     * <strong>Rollback</strong></p>
     *
     * @author Bruno
     */
    @Override
    public void rollback() {
        getEntityManager().getTransaction().rollback();
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    /**
     * <p>
     * <strong>Active Session</strong></p>
     *
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean activeSession() {
        return getEntityManager().getTransaction().isActive();
    }

    /**
     * <p>
     * <strong>Save</strong></p>
     *
     * @param object
     *
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean save(final Object object) {
        if (!activeSession()) {
            return false;
        }
        try {
            getEntityManager().persist(object);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    /**
     * <p>
     * <strong>Save transaction automatic</strong></p>
     *
     * @param object
     * @param transactionComplete
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean save(final Object object, boolean transactionComplete) {
        if (activeSession()) {
            return false;
        }
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(object);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    /**
     * <p>
     * <strong>Update</strong></p>
     *
     * @param objeto
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean update(final Object objeto) {
        if (!activeSession()) {
            return false;
        }
        Class classe = objeto.getClass();
        int id;
        try {
            Method metodo = classe.getMethod("getId", new Class[]{});
            id = (Integer) metodo.invoke(objeto, (Object[]) null);
            if (id == -1) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Objeto esta passando -1");
                return false;
            }
        } catch (IllegalAccessException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        } catch (NoSuchMethodException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        } catch (SecurityException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        } catch (InvocationTargetException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
        try {
            getEntityManager().merge(objeto);
            getEntityManager().flush();
            return true;
        } catch (EJBQLException e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    /**
     * <p>
     * <strong>Updatetransaction automatic</strong></p>
     *
     * @param objeto
     * @param transactionComplete
     *
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean update(final Object objeto, boolean transactionComplete) {
        if (activeSession()) {
            return false;
        }
        Class classe = objeto.getClass();
        int id;
        try {
            Method metodo = classe.getMethod("getId", new Class[]{});
            id = (Integer) metodo.invoke(objeto, (Object[]) null);
            if (id == -1) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Objeto esta passando -1");
                return false;
            }
        } catch (IllegalAccessException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        } catch (NoSuchMethodException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        } catch (SecurityException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        } catch (InvocationTargetException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(objeto);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    /**
     * <p>
     * <strong>Delete</strong></p>
     *
     * @param object
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean delete(final Object object) {
        if (!activeSession()) {
            return false;
        }
        try {
            getEntityManager().remove(find(object));
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    /**
     * <p>
     * <strong>Delete automatic</strong></p>
     *
     * @param object
     * @param transactionComplete
     *
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean delete(final Object object, boolean transactionComplete) {
        if (activeSession()) {
            return false;
        }
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(find(object));
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
    }

    @Override
    public Object rebind(Object object) {
        try {
            openTransaction();
            object = find(object);
            getEntityManager().merge(object);
            getEntityManager().refresh(object);
            getEntityManager().flush();
            commit();
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
        }
        return object;
    }

    @Override
    public void refresh(Object object) {
        try {
            openTransaction();
            object = find(object);
            getEntityManager().merge(object);
            getEntityManager().refresh(object);
            if (!getEntityManager().getTransaction().isActive()) {
                return;
            }
            getEntityManager().flush();
            commit();
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * <p>
     * <strong>Find Object</strong></p>
     * <p>
     * <strong>Exemplo:</strong>User user = new User(1, "Paul"); find(user);</p>
     *
     * @param object (Nome do objeto String)
     *
     * @author Bruno
     *
     * @return Object
     */
    @Override
    public Object find(final Object object) {
        return find(object, null);
    }

    /**
     * <p>
     * <strong>Find Object</strong></p>
     * <p>
     * <strong>Exemplo:</strong>find("User" or new User(), objectId); </p>
     *
     * @param object (Nome do objeto String)
     * @param objectId (Id a ser pesquisado)
     *
     * @author Bruno
     *
     * @return Object
     */
    @Override
    public Object find(Object object, final Object objectId) {
        if (object == null) {
            return null;
        }
        if (objectId == null) {
            int id;
            try {
                Class classe = object.getClass();
                Method metodo = classe.getMethod("getId", new Class[]{});
                id = (Integer) metodo.invoke(object, (Object[]) null);
                if (id == -1) {
                    return null;
                }
            } catch (IllegalAccessException e) {
                Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
                return null;
            } catch (IllegalArgumentException e) {
                Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
                return null;
            } catch (NoSuchMethodException e) {
                Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
                return null;
            } catch (SecurityException e) {
                Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
                return null;
            } catch (InvocationTargetException e) {
                Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
                return null;
            }
            object = getEntityManager().find(object.getClass(), id);
        } else {
            try {
                object = getEntityManager().find(object.getClass(), objectId);
            } catch (Exception e) {
                Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
                return null;
            }
        }
        return object;
    }

    /**
     * <p>
     * <strong>Find Object</strong></p>
     * <p>
     * <strong>Exemplo:</strong>find("User", new int[]{1,2,3,4,5}); </p>
     *
     * @param id (Lista de ids)
     * @param className (Nome da classe)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List find(String className, int id[]) {
        return find(className, id, "");
    }

    /**
     * <p>
     * <strong>Find Object</strong></p>
     * <p>
     * <strong>Exemplo:</strong>find("User", new int[]{1,2,3,4,5}, "id_people");
     * </p>
     *
     * @param id (Lista de ids)
     * @param className (Nome da classe)
     * @param field (Faz a consulta utilizando outro campo que não id como
     * parâmetro)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List find(String className, int id[], String field) {
        String stringCampo = "id";
        if (!field.isEmpty()) {
            stringCampo = field;
        }
        List list = new ArrayList<Object>();
        String queryPesquisaString = "";
        for (int i = 0; i < id.length; i++) {
            if (i == 0) {
                queryPesquisaString = Integer.toString(id[i]);
            } else {
                queryPesquisaString += ", " + Integer.toString(id[i]);
            }
            String queryString = "SELECT OB FROM " + className + " OB WHERE OB." + stringCampo + " IN (" + queryPesquisaString + ")";
            Query query = getEntityManager().createQuery(queryString);
            list = query.getResultList();
            if (list.isEmpty()) {
                return list;
            }
        }
        return list;
    }

    /**
     * <p>
     * <strong>List</strong></p>
     * <p>
     * <strong>Exemplo:</strong> list(new User()).</p>
     *
     * @param className (Nome do objeto String)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List list(Object className) {
        String name = className.getClass().getSimpleName();
        return list(name);
    }

    /**
     * <p>
     * <strong>List</strong></p>
     * <p>
     * <strong>Exemplo:</strong> list("User").</p>
     *
     * @param className (Nome do objeto String)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List list(String className) {
        List result = new ArrayList();
        String queryString = "SELECT OB FROM " + className + " AS OB";
        try {
            Query qry = getEntityManager().createQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
        }
        return result;
    }

    @Override
    public List list(Object className, boolean order) {
        return list(className.getClass().getSimpleName(), order);
    }

    /**
     * <p>
     * <strong>List</strong></p>
     * <p>
     * <strong>Exemplo:</strong> list("User", boolean (true or false)).</p>
     *
     * @param className (Nome do objeto String)
     * @param order [Se o resultado deve ser ordenado (Verificar se a namedQuery
     * esta na Classe/Entidade)]
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List list(String className, boolean order) {
        try {
            Query query = getEntityManager().createNamedQuery(className + ".findAll");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return new ArrayList();
        }
        return new ArrayList();
    }

    /**
     * <p>
     * <strong>List Query</strong></p>
     * <p>
     * <strong>Exemplo:</strong> E@NamedQuery(name = "Object.find", query =
     * "SELECT O FROM Object AS O WHERE O.id = :p1") Uso: listQuery(Object,
     * find, {1}) Exemplo 2 @NamedQuery(name = "Object.find", query = "SELECT O
     * FROM Object AS O WHERE O.id = :p1 AND O.description = :p2") Uso:
     * listQuery(Object, find, {1, 'Feliz'}).</p>
     *
     * @param className (Nome do objeto)
     * @param find (Nome da NamedQuery dentro do objeto)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List listQuery(Object className, String find) {
        return listQuery(className.getClass().getSimpleName(), find, new Object[]{});
    }
    
    /**
     * <p>
     * <strong>List Query</strong></p>
     * <p>
     * <strong>Exemplo:</strong> E@NamedQuery(name = "Object.find", query =
     * "SELECT O FROM Object AS O WHERE O.id = :p1") Uso: listQuery(Object,
     * find, {1}) Exemplo 2 @NamedQuery(name = "Object.find", query = "SELECT O
     * FROM Object AS O WHERE O.id = :p1 AND O.description = :p2") Uso:
     * listQuery(Object, find, {1, 'Feliz'}).</p>
     *
     * @param className (Nome do objeto)
     * @param find (Nome da NamedQuery dentro do objeto)
     * @param params (Cria se parâmetros organizados para realizar a consulta)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List listQuery(Object className, String find, Object[] params) {
        return listQuery(className.getClass().getSimpleName(), find, params);
    }

    /**
     * <p>
     * <strong>List Query</strong></p>
     * <p>
     * <strong>Exemplo:</strong> E@NamedQuery(name = "Object.find", query =
     * "SELECT O FROM Object AS O WHERE O.id = :p1") Uso: listQuery("Object",
     * find, {1}) Exemplo 2 @NamedQuery(name = "Object.find", query = "SELECT O
     * FROM Object AS O WHERE O.id = :p1 AND O.description = :p2") Uso:
     * listQuery(Object, find, {1, 'Feliz'}).</p>
     *
     * @param className (Nome do objeto)
     * @param find (Nome da NamedQuery dentro do objeto)
     * @param params (Cria se parâmetros organizados para realizar a consulta)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List listQuery(String className, String find, Object[] params) {
        try {
            Query query = getEntityManager().createNamedQuery(className + "." + find);
            int y = 1;
            for (Object param : params) {
                if (Types.isInteger(param)) {
                    try {
                        query.setParameter("p" + y, Integer.parseInt((String) param));
                    } catch (Exception e) {
                        query.setParameter("p" + y, param);
                    }
                } else if (Types.isFloat(param)) {
                    query.setParameter("p" + y, Float.parseFloat((String) param));
                } else if (Types.isDouble(param)) {
                    query.setParameter("p" + y, Double.parseDouble((String) param));
                } else {
                    query.setParameter("p" + y, (String) param);
                }
                y++;
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return new ArrayList();
        }
        return new ArrayList();
    }

    /**
     * <p>
     * <strong>Live List</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveList(SELECT U FROM User AS U);
     * Set nativeQuery = true. Caso não encontre nenhum resultado retorna uma
     * lista vazia; Se houver algum erro retorna lista vazia;</p>
     *
     * @param queryString
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List liveList(String queryString) {
        return liveList(queryString, false, 0);
    }

    /**
     * <p>
     * <strong>Live List</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveList(SELECT U FROM User AS U);
     * Set nativeQuery = true Exemplo 2: liveList(select * from user as u,
     * true); Set maxResults = 5. Caso não encontre nenhum resultado retorna uma
     * lista vazia; Se houver algum erro retorna lista vazia;</p>
     *
     * @param queryString
     * @param nativeQuery
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List liveList(String queryString, boolean nativeQuery) {
        return liveList(queryString, nativeQuery, 0);
    }

    /**
     * <p>
     * <strong>Live List</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveList(SELECT U FROM User AS U);
     * Set nativeQuery = true Exemplo 2: liveList(select * from user as u,
     * true); Set maxResults = 5 Exemplo 3: liveList(SELECT U FROM User AS U,
     * true, 5) Caso não encontre nenhum resultado retorna uma lista vazia; Se
     * houver algum erro retorna lista vazia;</p>
     *
     * @param queryString
     * @param nativeQuery
     * @param maxResults
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List liveList(String queryString, boolean nativeQuery, int maxResults) {
        try {
            Query query;
            if (nativeQuery) {
                query = getEntityManager().createNativeQuery(queryString);
            } else {
                query = getEntityManager().createQuery(queryString);
            }
            if (maxResults > 0) {
                query.setMaxResults(maxResults);
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    /**
     * <p>
     * <strong>Live Object</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveSingle(SELECT U FROM User AS U);
     * Set nativeQuery = true; Retorna somente um resultado, se houver mais de
     * um retornará null; Caso não encontre nenhum resultado retorna null;</p>
     *
     * @param queryString
     *
     * @author Bruno
     *
     * @return Object
     */
    @Override
    public Object liveSingle(String queryString) {
        return liveSingle(queryString, false);
    }

    /**
     * <p>
     * <strong>Live Object</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveSingle(SELECT U FROM User AS U);
     * Set nativeQuery = true Exemplo 2: liveSingle(select * from user as u,
     * true); Retorna somente um resultado, se houver mais de um retornará null;
     * Caso não encontre nenhum resultado retorna null;</p>
     *
     * @param queryString
     * @param nativeQuery
     *
     *
     * @author Bruno
     *
     * @return Object
     */
    @Override
    public Object liveSingle(String queryString, boolean nativeQuery) {
        try {
            Query query;
            if (nativeQuery) {
                query = getEntityManager().createNativeQuery(queryString);
            } else {
                query = getEntityManager().createQuery(queryString);
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                if (list.size() == 1) {
                    return query.getSingleResult();
                }
                return null;
            }
        } catch (Exception e) {

        }
        return null;
    }
}
