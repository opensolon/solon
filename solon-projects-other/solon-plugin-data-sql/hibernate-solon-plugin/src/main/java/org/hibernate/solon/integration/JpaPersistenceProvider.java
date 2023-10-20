package org.hibernate.solon.integration;

import org.hibernate.jpa.internal.util.PersistenceUtilHelper;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.LoadState;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;
import java.util.Map;

/**
 * 提供 java 配置的数据源支持
 *
 * @author noear
 * @since 2.5
 * */
public class JpaPersistenceProvider implements PersistenceProvider {
    private final PersistenceUtilHelper.MetadataCache cache = new PersistenceUtilHelper.MetadataCache();

    @Override
    public EntityManagerFactory createEntityManagerFactory(String unitName, Map map) {
        HibernateAdapter tmp = HibernateAdapterManager.getOnly(unitName);

        return tmp.getSessionFactory();
    }

    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo unitInfo, Map map) {
        HibernateAdapter tmp = HibernateAdapterManager.getOnly(unitInfo.getPersistenceUnitName());

        return tmp.getSessionFactory();
    }

    @Override
    public void generateSchema(PersistenceUnitInfo unitInfo, Map map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean generateSchema(String unitName, Map map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProviderUtil getProviderUtil() {
        return providerUtil;
    }

    private final ProviderUtil providerUtil = new ProviderUtil() {
        @Override
        public LoadState isLoadedWithoutReference(Object proxy, String property) {
            return PersistenceUtilHelper.isLoadedWithoutReference(proxy, property, cache);
        }

        @Override
        public LoadState isLoadedWithReference(Object proxy, String property) {
            return PersistenceUtilHelper.isLoadedWithReference(proxy, property, cache);
        }

        @Override
        public LoadState isLoaded(Object reference) {
            return PersistenceUtilHelper.isLoaded(reference);
        }
    };
}
