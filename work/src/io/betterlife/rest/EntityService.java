package io.betterlife.rest;

import io.betterlife.application.ApplicationConfig;
import io.betterlife.persistence.MetaDataManager;
import io.betterlife.persistence.BaseOperator;
import io.betterlife.persistence.NamedQueryRules;
import io.betterlife.util.EntityUtils;
import io.betterlife.util.jpa.OpenJPAUtil;
import io.betterlife.util.rest.ExecuteResult;
import io.betterlife.util.rest.IOUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Author: Lawrence Liu(xqinliu@cn.ibm.com)
 * Date: 11/2/14
 */
@Path("/")
@Stateless
public class EntityService {

    @PersistenceContext(unitName = ApplicationConfig.PersistenceUnitName)
    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger(EntityService.class.getName());

    private NamedQueryRules namedQueryRule;
    private BaseOperator operator;

    @GET
    @Path("/entity/{entityName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getEntityMeta(@PathParam("entityName") String entityName) throws IOException {
        logger.debug("Getting entity meta data for " + entityName);
        entityName = StringUtils.uncapitalize(entityName);
        MetaDataManager.getInstance().setAllFieldMetaData(entityManager);
        Map<String, Class> meta = MetaDataManager.getInstance().getMetaDataOfClass(
            ServiceEntityManager.getInstance().getServiceEntity(entityName));
        String result = new ExecuteResult<Map<String, Class>>().getRestString(meta);
        if (logger.isTraceEnabled()){
            logger.trace("Returning \n%s\n for entity[%s] meta", result, entityName);
        }
        return result;
    }


    @GET
    @Path("/{objectType}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getObjectByTypeAndId(@PathParam("id") long id,
                                       @PathParam("objectType") String objectType) throws IOException {
        return new ExecuteResult<>().getRestString(
            getOperator().getBaseObjectById(
                entityManager, id, getNamedQueryRule().getIdQueryForEntity(objectType)
            )
        );
    }

    @GET
    @Path("/{objectType}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllByObjectType(@PathParam("objectType") String objectType) throws IOException {
        List<Object> result = getOperator().getBaseObjects(
            entityManager,
            getNamedQueryRule().getAllQueryForEntity(objectType)
        );
        return new ExecuteResult<List<Object>>().getRestString(result);
    }

    @POST
    @Path("/{objectType}")
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String create(@PathParam("objectType") String objectType,
                         @Context HttpServletRequest request,
                         InputStream requestBody)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        Map<String, String> parameters = IOUtil.getInstance().inputStreamToJson(requestBody);
        Object obj = ServiceEntityManager.getInstance().entityObjectFromType(objectType);
        EntityUtils.getInstance().mapToBaseObject(entityManager, obj, parameters);
        getOperator().save(entityManager, obj);
        return new ExecuteResult<String>().getRestString("SUCCESS");
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public NamedQueryRules getNamedQueryRule(){
        if (null == this.namedQueryRule) {
            setNamedQueryRule(NamedQueryRules.getInstance());
        }
        return this.namedQueryRule;
    }

    public void setNamedQueryRule(NamedQueryRules rule) {
        this.namedQueryRule = rule;
    }

    public void setOperator(BaseOperator operator) {
        this.operator = operator;
    }

    public BaseOperator getOperator() {
        if (null == operator) {
            setOperator(BaseOperator.getInstance());
        }
        return operator;
    }
}
