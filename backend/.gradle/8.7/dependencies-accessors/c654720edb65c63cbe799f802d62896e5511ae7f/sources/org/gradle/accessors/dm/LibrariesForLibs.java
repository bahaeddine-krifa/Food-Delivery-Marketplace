package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final FlywayLibraryAccessors laccForFlywayLibraryAccessors = new FlywayLibraryAccessors(owner);
    private final JacksonLibraryAccessors laccForJacksonLibraryAccessors = new JacksonLibraryAccessors(owner);
    private final JakartaLibraryAccessors laccForJakartaLibraryAccessors = new JakartaLibraryAccessors(owner);
    private final JjwtLibraryAccessors laccForJjwtLibraryAccessors = new JjwtLibraryAccessors(owner);
    private final JunitLibraryAccessors laccForJunitLibraryAccessors = new JunitLibraryAccessors(owner);
    private final KotlinLibraryAccessors laccForKotlinLibraryAccessors = new KotlinLibraryAccessors(owner);
    private final KotlinxLibraryAccessors laccForKotlinxLibraryAccessors = new KotlinxLibraryAccessors(owner);
    private final LettuceLibraryAccessors laccForLettuceLibraryAccessors = new LettuceLibraryAccessors(owner);
    private final LogbackLibraryAccessors laccForLogbackLibraryAccessors = new LogbackLibraryAccessors(owner);
    private final LombokLibraryAccessors laccForLombokLibraryAccessors = new LombokLibraryAccessors(owner);
    private final MapstructLibraryAccessors laccForMapstructLibraryAccessors = new MapstructLibraryAccessors(owner);
    private final MockitoLibraryAccessors laccForMockitoLibraryAccessors = new MockitoLibraryAccessors(owner);
    private final NimbusLibraryAccessors laccForNimbusLibraryAccessors = new NimbusLibraryAccessors(owner);
    private final RestLibraryAccessors laccForRestLibraryAccessors = new RestLibraryAccessors(owner);
    private final Slf4jLibraryAccessors laccForSlf4jLibraryAccessors = new Slf4jLibraryAccessors(owner);
    private final SpringLibraryAccessors laccForSpringLibraryAccessors = new SpringLibraryAccessors(owner);
    private final SpringdocLibraryAccessors laccForSpringdocLibraryAccessors = new SpringdocLibraryAccessors(owner);
    private final SwaggerLibraryAccessors laccForSwaggerLibraryAccessors = new SwaggerLibraryAccessors(owner);
    private final TestcontainersLibraryAccessors laccForTestcontainersLibraryAccessors = new TestcontainersLibraryAccessors(owner);
    private final UuidLibraryAccessors laccForUuidLibraryAccessors = new UuidLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Dependency provider for <b>assertj</b> with <b>org.assertj:assertj-core</b> coordinates and
     * with version reference <b>assertj</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getAssertj() {
        return create("assertj");
    }

    /**
     * Dependency provider for <b>hikaricp</b> with <b>com.zaxxer:HikariCP</b> coordinates and
     * with version reference <b>hikaricp</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getHikaricp() {
        return create("hikaricp");
    }

    /**
     * Dependency provider for <b>mockk</b> with <b>io.mockk:mockk</b> coordinates and
     * with version reference <b>mockk</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getMockk() {
        return create("mockk");
    }

    /**
     * Dependency provider for <b>postgresql</b> with <b>org.postgresql:postgresql</b> coordinates and
     * with version reference <b>postgresql</b>
     * <p>
     * This dependency was declared in catalog libs.versions.toml
     */
    public Provider<MinimalExternalModuleDependency> getPostgresql() {
        return create("postgresql");
    }

    /**
     * Group of libraries at <b>flyway</b>
     */
    public FlywayLibraryAccessors getFlyway() {
        return laccForFlywayLibraryAccessors;
    }

    /**
     * Group of libraries at <b>jackson</b>
     */
    public JacksonLibraryAccessors getJackson() {
        return laccForJacksonLibraryAccessors;
    }

    /**
     * Group of libraries at <b>jakarta</b>
     */
    public JakartaLibraryAccessors getJakarta() {
        return laccForJakartaLibraryAccessors;
    }

    /**
     * Group of libraries at <b>jjwt</b>
     */
    public JjwtLibraryAccessors getJjwt() {
        return laccForJjwtLibraryAccessors;
    }

    /**
     * Group of libraries at <b>junit</b>
     */
    public JunitLibraryAccessors getJunit() {
        return laccForJunitLibraryAccessors;
    }

    /**
     * Group of libraries at <b>kotlin</b>
     */
    public KotlinLibraryAccessors getKotlin() {
        return laccForKotlinLibraryAccessors;
    }

    /**
     * Group of libraries at <b>kotlinx</b>
     */
    public KotlinxLibraryAccessors getKotlinx() {
        return laccForKotlinxLibraryAccessors;
    }

    /**
     * Group of libraries at <b>lettuce</b>
     */
    public LettuceLibraryAccessors getLettuce() {
        return laccForLettuceLibraryAccessors;
    }

    /**
     * Group of libraries at <b>logback</b>
     */
    public LogbackLibraryAccessors getLogback() {
        return laccForLogbackLibraryAccessors;
    }

    /**
     * Group of libraries at <b>lombok</b>
     */
    public LombokLibraryAccessors getLombok() {
        return laccForLombokLibraryAccessors;
    }

    /**
     * Group of libraries at <b>mapstruct</b>
     */
    public MapstructLibraryAccessors getMapstruct() {
        return laccForMapstructLibraryAccessors;
    }

    /**
     * Group of libraries at <b>mockito</b>
     */
    public MockitoLibraryAccessors getMockito() {
        return laccForMockitoLibraryAccessors;
    }

    /**
     * Group of libraries at <b>nimbus</b>
     */
    public NimbusLibraryAccessors getNimbus() {
        return laccForNimbusLibraryAccessors;
    }

    /**
     * Group of libraries at <b>rest</b>
     */
    public RestLibraryAccessors getRest() {
        return laccForRestLibraryAccessors;
    }

    /**
     * Group of libraries at <b>slf4j</b>
     */
    public Slf4jLibraryAccessors getSlf4j() {
        return laccForSlf4jLibraryAccessors;
    }

    /**
     * Group of libraries at <b>spring</b>
     */
    public SpringLibraryAccessors getSpring() {
        return laccForSpringLibraryAccessors;
    }

    /**
     * Group of libraries at <b>springdoc</b>
     */
    public SpringdocLibraryAccessors getSpringdoc() {
        return laccForSpringdocLibraryAccessors;
    }

    /**
     * Group of libraries at <b>swagger</b>
     */
    public SwaggerLibraryAccessors getSwagger() {
        return laccForSwaggerLibraryAccessors;
    }

    /**
     * Group of libraries at <b>testcontainers</b>
     */
    public TestcontainersLibraryAccessors getTestcontainers() {
        return laccForTestcontainersLibraryAccessors;
    }

    /**
     * Group of libraries at <b>uuid</b>
     */
    public UuidLibraryAccessors getUuid() {
        return laccForUuidLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class FlywayLibraryAccessors extends SubDependencyFactory {
        private final FlywayDatabaseLibraryAccessors laccForFlywayDatabaseLibraryAccessors = new FlywayDatabaseLibraryAccessors(owner);

        public FlywayLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>org.flywaydb:flyway-core</b> coordinates and
         * with version reference <b>flyway</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("flyway.core");
        }

        /**
         * Group of libraries at <b>flyway.database</b>
         */
        public FlywayDatabaseLibraryAccessors getDatabase() {
            return laccForFlywayDatabaseLibraryAccessors;
        }

    }

    public static class FlywayDatabaseLibraryAccessors extends SubDependencyFactory {

        public FlywayDatabaseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>postgresql</b> with <b>org.flywaydb:flyway-database-postgresql</b> coordinates and
         * with version reference <b>flyway</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPostgresql() {
            return create("flyway.database.postgresql");
        }

    }

    public static class JacksonLibraryAccessors extends SubDependencyFactory {
        private final JacksonDatatypeLibraryAccessors laccForJacksonDatatypeLibraryAccessors = new JacksonDatatypeLibraryAccessors(owner);
        private final JacksonModuleLibraryAccessors laccForJacksonModuleLibraryAccessors = new JacksonModuleLibraryAccessors(owner);

        public JacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>jackson.datatype</b>
         */
        public JacksonDatatypeLibraryAccessors getDatatype() {
            return laccForJacksonDatatypeLibraryAccessors;
        }

        /**
         * Group of libraries at <b>jackson.module</b>
         */
        public JacksonModuleLibraryAccessors getModule() {
            return laccForJacksonModuleLibraryAccessors;
        }

    }

    public static class JacksonDatatypeLibraryAccessors extends SubDependencyFactory {

        public JacksonDatatypeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>hibernate</b> with <b>com.fasterxml.jackson.datatype:jackson-datatype-hibernate</b> coordinates and
         * with version reference <b>jackson</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getHibernate() {
            return create("jackson.datatype.hibernate");
        }

        /**
         * Dependency provider for <b>jsr310</b> with <b>com.fasterxml.jackson.datatype:jackson-datatype-jsr310</b> coordinates and
         * with version reference <b>jackson</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJsr310() {
            return create("jackson.datatype.jsr310");
        }

    }

    public static class JacksonModuleLibraryAccessors extends SubDependencyFactory {

        public JacksonModuleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>kotlin</b> with <b>com.fasterxml.jackson.module:jackson-module-kotlin</b> coordinates and
         * with version reference <b>jacksonKotlin</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKotlin() {
            return create("jackson.module.kotlin");
        }

    }

    public static class JakartaLibraryAccessors extends SubDependencyFactory {

        public JakartaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>mail</b> with <b>com.sun.mail:jakarta.mail</b> coordinates and
         * with version reference <b>jakartaMail</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMail() {
            return create("jakarta.mail");
        }

    }

    public static class JjwtLibraryAccessors extends SubDependencyFactory {

        public JjwtLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>io.jsonwebtoken:jjwt-api</b> coordinates and
         * with version reference <b>jjwt</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("jjwt.api");
        }

        /**
         * Dependency provider for <b>impl</b> with <b>io.jsonwebtoken:jjwt-impl</b> coordinates and
         * with version reference <b>jjwt</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getImpl() {
            return create("jjwt.impl");
        }

        /**
         * Dependency provider for <b>jackson</b> with <b>io.jsonwebtoken:jjwt-jackson</b> coordinates and
         * with version reference <b>jjwt</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJackson() {
            return create("jjwt.jackson");
        }

    }

    public static class JunitLibraryAccessors extends SubDependencyFactory {
        private final JunitJupiterLibraryAccessors laccForJunitJupiterLibraryAccessors = new JunitJupiterLibraryAccessors(owner);

        public JunitLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>junit.jupiter</b>
         */
        public JunitJupiterLibraryAccessors getJupiter() {
            return laccForJunitJupiterLibraryAccessors;
        }

    }

    public static class JunitJupiterLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public JunitJupiterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jupiter</b> with <b>org.junit.jupiter:junit-jupiter</b> coordinates and
         * with version reference <b>junit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("junit.jupiter");
        }

        /**
         * Dependency provider for <b>params</b> with <b>org.junit.jupiter:junit-jupiter-params</b> coordinates and
         * with version reference <b>junit</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getParams() {
            return create("junit.jupiter.params");
        }

    }

    public static class KotlinLibraryAccessors extends SubDependencyFactory {

        public KotlinLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>reflect</b> with <b>org.jetbrains.kotlin:kotlin-reflect</b> coordinates and
         * with version reference <b>kotlin</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getReflect() {
            return create("kotlin.reflect");
        }

        /**
         * Dependency provider for <b>stdlib</b> with <b>org.jetbrains.kotlin:kotlin-stdlib</b> coordinates and
         * with version reference <b>kotlin</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getStdlib() {
            return create("kotlin.stdlib");
        }

    }

    public static class KotlinxLibraryAccessors extends SubDependencyFactory {
        private final KotlinxCoroutinesLibraryAccessors laccForKotlinxCoroutinesLibraryAccessors = new KotlinxCoroutinesLibraryAccessors(owner);
        private final KotlinxSerializationLibraryAccessors laccForKotlinxSerializationLibraryAccessors = new KotlinxSerializationLibraryAccessors(owner);

        public KotlinxLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>kotlinx.coroutines</b>
         */
        public KotlinxCoroutinesLibraryAccessors getCoroutines() {
            return laccForKotlinxCoroutinesLibraryAccessors;
        }

        /**
         * Group of libraries at <b>kotlinx.serialization</b>
         */
        public KotlinxSerializationLibraryAccessors getSerialization() {
            return laccForKotlinxSerializationLibraryAccessors;
        }

    }

    public static class KotlinxCoroutinesLibraryAccessors extends SubDependencyFactory {

        public KotlinxCoroutinesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>org.jetbrains.kotlinx:kotlinx-coroutines-core</b> coordinates and
         * with version <b>1.7.3</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("kotlinx.coroutines.core");
        }

        /**
         * Dependency provider for <b>reactor</b> with <b>org.jetbrains.kotlinx:kotlinx-coroutines-reactor</b> coordinates and
         * with version <b>1.7.3</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getReactor() {
            return create("kotlinx.coroutines.reactor");
        }

    }

    public static class KotlinxSerializationLibraryAccessors extends SubDependencyFactory {

        public KotlinxSerializationLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>json</b> with <b>org.jetbrains.kotlinx:kotlinx-serialization-json</b> coordinates and
         * with version reference <b>kotlinxSerialization</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJson() {
            return create("kotlinx.serialization.json");
        }

    }

    public static class LettuceLibraryAccessors extends SubDependencyFactory {

        public LettuceLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>io.lettuce:lettuce-core</b> coordinates and
         * with version reference <b>lettuce</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("lettuce.core");
        }

    }

    public static class LogbackLibraryAccessors extends SubDependencyFactory {

        public LogbackLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>classic</b> with <b>ch.qos.logback:logback-classic</b> coordinates and
         * with version reference <b>logback</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getClassic() {
            return create("logback.classic");
        }

    }

    public static class LombokLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {
        private final LombokMapstructLibraryAccessors laccForLombokMapstructLibraryAccessors = new LombokMapstructLibraryAccessors(owner);

        public LombokLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>lombok</b> with <b>org.projectlombok:lombok</b> coordinates and
         * with version reference <b>lombok</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("lombok");
        }

        /**
         * Group of libraries at <b>lombok.mapstruct</b>
         */
        public LombokMapstructLibraryAccessors getMapstruct() {
            return laccForLombokMapstructLibraryAccessors;
        }

    }

    public static class LombokMapstructLibraryAccessors extends SubDependencyFactory {

        public LombokMapstructLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>binding</b> with <b>org.projectlombok:lombok-mapstruct-binding</b> coordinates and
         * with version <b>0.2.0</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getBinding() {
            return create("lombok.mapstruct.binding");
        }

    }

    public static class MapstructLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public MapstructLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>mapstruct</b> with <b>org.mapstruct:mapstruct</b> coordinates and
         * with version reference <b>mapstruct</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("mapstruct");
        }

        /**
         * Dependency provider for <b>processor</b> with <b>org.mapstruct:mapstruct-processor</b> coordinates and
         * with version reference <b>mapstructProcessor</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getProcessor() {
            return create("mapstruct.processor");
        }

    }

    public static class MockitoLibraryAccessors extends SubDependencyFactory {
        private final MockitoJunitLibraryAccessors laccForMockitoJunitLibraryAccessors = new MockitoJunitLibraryAccessors(owner);

        public MockitoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>core</b> with <b>org.mockito:mockito-core</b> coordinates and
         * with version reference <b>mockito</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCore() {
            return create("mockito.core");
        }

        /**
         * Group of libraries at <b>mockito.junit</b>
         */
        public MockitoJunitLibraryAccessors getJunit() {
            return laccForMockitoJunitLibraryAccessors;
        }

    }

    public static class MockitoJunitLibraryAccessors extends SubDependencyFactory {

        public MockitoJunitLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jupiter</b> with <b>org.mockito:mockito-junit-jupiter</b> coordinates and
         * with version reference <b>mockito</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJupiter() {
            return create("mockito.junit.jupiter");
        }

    }

    public static class NimbusLibraryAccessors extends SubDependencyFactory {
        private final NimbusJoseLibraryAccessors laccForNimbusJoseLibraryAccessors = new NimbusJoseLibraryAccessors(owner);

        public NimbusLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>nimbus.jose</b>
         */
        public NimbusJoseLibraryAccessors getJose() {
            return laccForNimbusJoseLibraryAccessors;
        }

    }

    public static class NimbusJoseLibraryAccessors extends SubDependencyFactory {

        public NimbusJoseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jwt</b> with <b>com.nimbusds:nimbus-jose-jwt</b> coordinates and
         * with version reference <b>nimbusJoseJwt</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJwt() {
            return create("nimbus.jose.jwt");
        }

    }

    public static class RestLibraryAccessors extends SubDependencyFactory {

        public RestLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>assured</b> with <b>io.rest-assured:rest-assured</b> coordinates and
         * with version reference <b>restAssured</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAssured() {
            return create("rest.assured");
        }

    }

    public static class Slf4jLibraryAccessors extends SubDependencyFactory {

        public Slf4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>org.slf4j:slf4j-api</b> coordinates and
         * with version reference <b>slf4j</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("slf4j.api");
        }

    }

    public static class SpringLibraryAccessors extends SubDependencyFactory {
        private final SpringBootLibraryAccessors laccForSpringBootLibraryAccessors = new SpringBootLibraryAccessors(owner);
        private final SpringSecurityLibraryAccessors laccForSpringSecurityLibraryAccessors = new SpringSecurityLibraryAccessors(owner);

        public SpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>spring.boot</b>
         */
        public SpringBootLibraryAccessors getBoot() {
            return laccForSpringBootLibraryAccessors;
        }

        /**
         * Group of libraries at <b>spring.security</b>
         */
        public SpringSecurityLibraryAccessors getSecurity() {
            return laccForSpringSecurityLibraryAccessors;
        }

    }

    public static class SpringBootLibraryAccessors extends SubDependencyFactory {
        private final SpringBootStarterLibraryAccessors laccForSpringBootStarterLibraryAccessors = new SpringBootStarterLibraryAccessors(owner);

        public SpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>spring.boot.starter</b>
         */
        public SpringBootStarterLibraryAccessors getStarter() {
            return laccForSpringBootStarterLibraryAccessors;
        }

    }

    public static class SpringBootStarterLibraryAccessors extends SubDependencyFactory {
        private final SpringBootStarterDataLibraryAccessors laccForSpringBootStarterDataLibraryAccessors = new SpringBootStarterDataLibraryAccessors(owner);

        public SpringBootStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>actuator</b> with <b>org.springframework.boot:spring-boot-starter-actuator</b> coordinates and
         * with version reference <b>springBoot</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getActuator() {
            return create("spring.boot.starter.actuator");
        }

        /**
         * Dependency provider for <b>mail</b> with <b>org.springframework.boot:spring-boot-starter-mail</b> coordinates and
         * with version reference <b>springBoot</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMail() {
            return create("spring.boot.starter.mail");
        }

        /**
         * Dependency provider for <b>security</b> with <b>org.springframework.boot:spring-boot-starter-security</b> coordinates and
         * with version reference <b>springBoot</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSecurity() {
            return create("spring.boot.starter.security");
        }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.boot:spring-boot-starter-test</b> coordinates and
         * with version reference <b>springBoot</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("spring.boot.starter.test");
        }

        /**
         * Dependency provider for <b>validation</b> with <b>org.springframework.boot:spring-boot-starter-validation</b> coordinates and
         * with version reference <b>springBoot</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getValidation() {
            return create("spring.boot.starter.validation");
        }

        /**
         * Dependency provider for <b>web</b> with <b>org.springframework.boot:spring-boot-starter-web</b> coordinates and
         * with version reference <b>springBoot</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getWeb() {
            return create("spring.boot.starter.web");
        }

        /**
         * Dependency provider for <b>websocket</b> with <b>org.springframework.boot:spring-boot-starter-websocket</b> coordinates and
         * with version reference <b>springBoot</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getWebsocket() {
            return create("spring.boot.starter.websocket");
        }

        /**
         * Group of libraries at <b>spring.boot.starter.data</b>
         */
        public SpringBootStarterDataLibraryAccessors getData() {
            return laccForSpringBootStarterDataLibraryAccessors;
        }

    }

    public static class SpringBootStarterDataLibraryAccessors extends SubDependencyFactory {

        public SpringBootStarterDataLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jpa</b> with <b>org.springframework.boot:spring-boot-starter-data-jpa</b> coordinates and
         * with version reference <b>springBoot</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJpa() {
            return create("spring.boot.starter.data.jpa");
        }

        /**
         * Dependency provider for <b>redis</b> with <b>org.springframework.boot:spring-boot-starter-data-redis</b> coordinates and
         * with version reference <b>springBoot</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRedis() {
            return create("spring.boot.starter.data.redis");
        }

    }

    public static class SpringSecurityLibraryAccessors extends SubDependencyFactory {
        private final SpringSecurityOauth2LibraryAccessors laccForSpringSecurityOauth2LibraryAccessors = new SpringSecurityOauth2LibraryAccessors(owner);

        public SpringSecurityLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>config</b> with <b>org.springframework.security:spring-security-config</b> coordinates and
         * with version reference <b>springSecurity</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getConfig() {
            return create("spring.security.config");
        }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.security:spring-security-test</b> coordinates and
         * with version reference <b>springSecurity</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("spring.security.test");
        }

        /**
         * Dependency provider for <b>web</b> with <b>org.springframework.security:spring-security-web</b> coordinates and
         * with version reference <b>springSecurity</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getWeb() {
            return create("spring.security.web");
        }

        /**
         * Group of libraries at <b>spring.security.oauth2</b>
         */
        public SpringSecurityOauth2LibraryAccessors getOauth2() {
            return laccForSpringSecurityOauth2LibraryAccessors;
        }

    }

    public static class SpringSecurityOauth2LibraryAccessors extends SubDependencyFactory {
        private final SpringSecurityOauth2ResourceLibraryAccessors laccForSpringSecurityOauth2ResourceLibraryAccessors = new SpringSecurityOauth2ResourceLibraryAccessors(owner);

        public SpringSecurityOauth2LibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jose</b> with <b>org.springframework.security:spring-security-oauth2-jose</b> coordinates and
         * with version reference <b>springSecurity</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJose() {
            return create("spring.security.oauth2.jose");
        }

        /**
         * Group of libraries at <b>spring.security.oauth2.resource</b>
         */
        public SpringSecurityOauth2ResourceLibraryAccessors getResource() {
            return laccForSpringSecurityOauth2ResourceLibraryAccessors;
        }

    }

    public static class SpringSecurityOauth2ResourceLibraryAccessors extends SubDependencyFactory {

        public SpringSecurityOauth2ResourceLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>server</b> with <b>org.springframework.security:spring-security-oauth2-resource-server</b> coordinates and
         * with version reference <b>springSecurity</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getServer() {
            return create("spring.security.oauth2.resource.server");
        }

    }

    public static class SpringdocLibraryAccessors extends SubDependencyFactory {
        private final SpringdocOpenapiLibraryAccessors laccForSpringdocOpenapiLibraryAccessors = new SpringdocOpenapiLibraryAccessors(owner);

        public SpringdocLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>springdoc.openapi</b>
         */
        public SpringdocOpenapiLibraryAccessors getOpenapi() {
            return laccForSpringdocOpenapiLibraryAccessors;
        }

    }

    public static class SpringdocOpenapiLibraryAccessors extends SubDependencyFactory {
        private final SpringdocOpenapiStarterLibraryAccessors laccForSpringdocOpenapiStarterLibraryAccessors = new SpringdocOpenapiStarterLibraryAccessors(owner);

        public SpringdocOpenapiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>springdoc.openapi.starter</b>
         */
        public SpringdocOpenapiStarterLibraryAccessors getStarter() {
            return laccForSpringdocOpenapiStarterLibraryAccessors;
        }

    }

    public static class SpringdocOpenapiStarterLibraryAccessors extends SubDependencyFactory {

        public SpringdocOpenapiStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>common</b> with <b>org.springdoc:springdoc-openapi-starter-common</b> coordinates and
         * with version reference <b>springdoc</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCommon() {
            return create("springdoc.openapi.starter.common");
        }

        /**
         * Dependency provider for <b>webmvc</b> with <b>org.springdoc:springdoc-openapi-starter-webmvc-ui</b> coordinates and
         * with version reference <b>springdoc</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getWebmvc() {
            return create("springdoc.openapi.starter.webmvc");
        }

    }

    public static class SwaggerLibraryAccessors extends SubDependencyFactory {
        private final SwaggerCoreLibraryAccessors laccForSwaggerCoreLibraryAccessors = new SwaggerCoreLibraryAccessors(owner);

        public SwaggerLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>swagger.core</b>
         */
        public SwaggerCoreLibraryAccessors getCore() {
            return laccForSwaggerCoreLibraryAccessors;
        }

    }

    public static class SwaggerCoreLibraryAccessors extends SubDependencyFactory {

        public SwaggerCoreLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jakarta</b> with <b>io.swagger.core.v3:swagger-core-jakarta</b> coordinates and
         * with version <b>2.2.20</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJakarta() {
            return create("swagger.core.jakarta");
        }

    }

    public static class TestcontainersLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {
        private final TestcontainersJunitLibraryAccessors laccForTestcontainersJunitLibraryAccessors = new TestcontainersJunitLibraryAccessors(owner);

        public TestcontainersLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>testcontainers</b> with <b>org.testcontainers:testcontainers</b> coordinates and
         * with version reference <b>testcontainers</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> asProvider() {
            return create("testcontainers");
        }

        /**
         * Dependency provider for <b>postgresql</b> with <b>org.testcontainers:postgresql</b> coordinates and
         * with version reference <b>testcontainers</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPostgresql() {
            return create("testcontainers.postgresql");
        }

        /**
         * Group of libraries at <b>testcontainers.junit</b>
         */
        public TestcontainersJunitLibraryAccessors getJunit() {
            return laccForTestcontainersJunitLibraryAccessors;
        }

    }

    public static class TestcontainersJunitLibraryAccessors extends SubDependencyFactory {

        public TestcontainersJunitLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jupiter</b> with <b>org.testcontainers:junit-jupiter</b> coordinates and
         * with version reference <b>testcontainers</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJupiter() {
            return create("testcontainers.junit.jupiter");
        }

    }

    public static class UuidLibraryAccessors extends SubDependencyFactory {

        public UuidLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>creator</b> with <b>com.github.stephenc.jcip:jcip-annotations</b> coordinates and
         * with version <b>1.0-1</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getCreator() {
            return create("uuid.creator");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>assertj</b> with value <b>3.25.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAssertj() { return getVersion("assertj"); }

        /**
         * Version alias <b>bcrypt</b> with value <b>0.10.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getBcrypt() { return getVersion("bcrypt"); }

        /**
         * Version alias <b>flyway</b> with value <b>10.17.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getFlyway() { return getVersion("flyway"); }

        /**
         * Version alias <b>gradle</b> with value <b>8.5</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGradle() { return getVersion("gradle"); }

        /**
         * Version alias <b>hikaricp</b> with value <b>5.1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getHikaricp() { return getVersion("hikaricp"); }

        /**
         * Version alias <b>jackson</b> with value <b>2.17.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJackson() { return getVersion("jackson"); }

        /**
         * Version alias <b>jacksonKotlin</b> with value <b>2.17.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJacksonKotlin() { return getVersion("jacksonKotlin"); }

        /**
         * Version alias <b>jakartaMail</b> with value <b>2.0.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJakartaMail() { return getVersion("jakartaMail"); }

        /**
         * Version alias <b>jjwt</b> with value <b>0.12.5</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJjwt() { return getVersion("jjwt"); }

        /**
         * Version alias <b>junit</b> with value <b>5.10.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJunit() { return getVersion("junit"); }

        /**
         * Version alias <b>kotlin</b> with value <b>1.9.24</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getKotlin() { return getVersion("kotlin"); }

        /**
         * Version alias <b>kotlinxSerialization</b> with value <b>1.6.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getKotlinxSerialization() { return getVersion("kotlinxSerialization"); }

        /**
         * Version alias <b>lettuce</b> with value <b>6.3.1.RELEASE</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLettuce() { return getVersion("lettuce"); }

        /**
         * Version alias <b>liquibase</b> with value <b>4.29.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLiquibase() { return getVersion("liquibase"); }

        /**
         * Version alias <b>logback</b> with value <b>1.5.6</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLogback() { return getVersion("logback"); }

        /**
         * Version alias <b>lombok</b> with value <b>1.18.32</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLombok() { return getVersion("lombok"); }

        /**
         * Version alias <b>mapstruct</b> with value <b>1.5.5.Final</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMapstruct() { return getVersion("mapstruct"); }

        /**
         * Version alias <b>mapstructProcessor</b> with value <b>1.5.5.Final</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMapstructProcessor() { return getVersion("mapstructProcessor"); }

        /**
         * Version alias <b>mockito</b> with value <b>5.11.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMockito() { return getVersion("mockito"); }

        /**
         * Version alias <b>mockk</b> with value <b>1.13.13</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getMockk() { return getVersion("mockk"); }

        /**
         * Version alias <b>nimbusJoseJwt</b> with value <b>9.37.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getNimbusJoseJwt() { return getVersion("nimbusJoseJwt"); }

        /**
         * Version alias <b>postgresql</b> with value <b>42.7.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getPostgresql() { return getVersion("postgresql"); }

        /**
         * Version alias <b>redis</b> with value <b>3.2.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRedis() { return getVersion("redis"); }

        /**
         * Version alias <b>restAssured</b> with value <b>5.4.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getRestAssured() { return getVersion("restAssured"); }

        /**
         * Version alias <b>slf4j</b> with value <b>2.0.13</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSlf4j() { return getVersion("slf4j"); }

        /**
         * Version alias <b>springBoot</b> with value <b>3.2.5</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpringBoot() { return getVersion("springBoot"); }

        /**
         * Version alias <b>springDataJpa</b> with value <b>3.2.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpringDataJpa() { return getVersion("springDataJpa"); }

        /**
         * Version alias <b>springFramework</b> with value <b>6.1.6</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpringFramework() { return getVersion("springFramework"); }

        /**
         * Version alias <b>springMail</b> with value <b>6.1.6</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpringMail() { return getVersion("springMail"); }

        /**
         * Version alias <b>springSecurity</b> with value <b>6.2.4</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpringSecurity() { return getVersion("springSecurity"); }

        /**
         * Version alias <b>springValidation</b> with value <b>3.2.3</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpringValidation() { return getVersion("springValidation"); }

        /**
         * Version alias <b>springWeb</b> with value <b>6.1.6</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpringWeb() { return getVersion("springWeb"); }

        /**
         * Version alias <b>springdoc</b> with value <b>2.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getSpringdoc() { return getVersion("springdoc"); }

        /**
         * Version alias <b>testcontainers</b> with value <b>1.19.8</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTestcontainers() { return getVersion("testcontainers"); }

        /**
         * Version alias <b>testcontainersJunit</b> with value <b>1.19.8</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTestcontainersJunit() { return getVersion("testcontainersJunit"); }

        /**
         * Version alias <b>uuid</b> with value <b>4.5.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getUuid() { return getVersion("uuid"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {
        private final DependencyPluginAccessors paccForDependencyPluginAccessors = new DependencyPluginAccessors(providers, config);
        private final JunitPluginAccessors paccForJunitPluginAccessors = new JunitPluginAccessors(providers, config);
        private final KotlinPluginAccessors paccForKotlinPluginAccessors = new KotlinPluginAccessors(providers, config);
        private final SpringPluginAccessors paccForSpringPluginAccessors = new SpringPluginAccessors(providers, config);

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>lombok</b> with plugin id <b>io.freefair.lombok</b> and
         * with version reference <b>lombok</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getLombok() { return createPlugin("lombok"); }

        /**
         * Group of plugins at <b>plugins.dependency</b>
         */
        public DependencyPluginAccessors getDependency() {
            return paccForDependencyPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.junit</b>
         */
        public JunitPluginAccessors getJunit() {
            return paccForJunitPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.kotlin</b>
         */
        public KotlinPluginAccessors getKotlin() {
            return paccForKotlinPluginAccessors;
        }

        /**
         * Group of plugins at <b>plugins.spring</b>
         */
        public SpringPluginAccessors getSpring() {
            return paccForSpringPluginAccessors;
        }

    }

    public static class DependencyPluginAccessors extends PluginFactory {

        public DependencyPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>dependency.management</b> with plugin id <b>io.spring.dependency-management</b> and
         * with version <b>1.1.6</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getManagement() { return createPlugin("dependency.management"); }

    }

    public static class JunitPluginAccessors extends PluginFactory {

        public JunitPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>junit.platform</b> with plugin id <b>org.junit.platform.gradle.plugin</b> and
         * with version <b>1.10.2</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getPlatform() { return createPlugin("junit.platform"); }

    }

    public static class KotlinPluginAccessors extends PluginFactory {

        public KotlinPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>kotlin.jpa</b> with plugin id <b>org.jetbrains.kotlin.plugin.jpa</b> and
         * with version reference <b>kotlin</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getJpa() { return createPlugin("kotlin.jpa"); }

        /**
         * Plugin provider for <b>kotlin.jvm</b> with plugin id <b>org.jetbrains.kotlin.jvm</b> and
         * with version reference <b>kotlin</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getJvm() { return createPlugin("kotlin.jvm"); }

        /**
         * Plugin provider for <b>kotlin.serialization</b> with plugin id <b>org.jetbrains.kotlin.plugin.serialization</b> and
         * with version reference <b>kotlin</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getSerialization() { return createPlugin("kotlin.serialization"); }

        /**
         * Plugin provider for <b>kotlin.spring</b> with plugin id <b>org.jetbrains.kotlin.plugin.spring</b> and
         * with version reference <b>kotlin</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getSpring() { return createPlugin("kotlin.spring"); }

    }

    public static class SpringPluginAccessors extends PluginFactory {

        public SpringPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Plugin provider for <b>spring.boot</b> with plugin id <b>org.springframework.boot</b> and
         * with version reference <b>springBoot</b>
         * <p>
         * This plugin was declared in catalog libs.versions.toml
         */
        public Provider<PluginDependency> getBoot() { return createPlugin("spring.boot"); }

    }

}
