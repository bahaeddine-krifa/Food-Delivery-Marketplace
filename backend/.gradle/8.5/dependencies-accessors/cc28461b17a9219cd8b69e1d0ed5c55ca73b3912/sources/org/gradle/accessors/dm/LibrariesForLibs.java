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
 * A catalog of dependencies accessible via the `libs` extension.
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
         * Creates a dependency provider for assertj (org.assertj:assertj-core)
     * with versionRef 'assertj'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAssertj() {
            return create("assertj");
    }

        /**
         * Creates a dependency provider for hikaricp (com.zaxxer:HikariCP)
     * with versionRef 'hikaricp'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getHikaricp() {
            return create("hikaricp");
    }

        /**
         * Creates a dependency provider for mockk (io.mockk:mockk)
     * with versionRef 'mockk'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMockk() {
            return create("mockk");
    }

        /**
         * Creates a dependency provider for postgresql (org.postgresql:postgresql)
     * with versionRef 'postgresql'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPostgresql() {
            return create("postgresql");
    }

    /**
     * Returns the group of libraries at flyway
     */
    public FlywayLibraryAccessors getFlyway() {
        return laccForFlywayLibraryAccessors;
    }

    /**
     * Returns the group of libraries at jackson
     */
    public JacksonLibraryAccessors getJackson() {
        return laccForJacksonLibraryAccessors;
    }

    /**
     * Returns the group of libraries at jakarta
     */
    public JakartaLibraryAccessors getJakarta() {
        return laccForJakartaLibraryAccessors;
    }

    /**
     * Returns the group of libraries at jjwt
     */
    public JjwtLibraryAccessors getJjwt() {
        return laccForJjwtLibraryAccessors;
    }

    /**
     * Returns the group of libraries at junit
     */
    public JunitLibraryAccessors getJunit() {
        return laccForJunitLibraryAccessors;
    }

    /**
     * Returns the group of libraries at kotlin
     */
    public KotlinLibraryAccessors getKotlin() {
        return laccForKotlinLibraryAccessors;
    }

    /**
     * Returns the group of libraries at kotlinx
     */
    public KotlinxLibraryAccessors getKotlinx() {
        return laccForKotlinxLibraryAccessors;
    }

    /**
     * Returns the group of libraries at lettuce
     */
    public LettuceLibraryAccessors getLettuce() {
        return laccForLettuceLibraryAccessors;
    }

    /**
     * Returns the group of libraries at logback
     */
    public LogbackLibraryAccessors getLogback() {
        return laccForLogbackLibraryAccessors;
    }

    /**
     * Returns the group of libraries at lombok
     */
    public LombokLibraryAccessors getLombok() {
        return laccForLombokLibraryAccessors;
    }

    /**
     * Returns the group of libraries at mapstruct
     */
    public MapstructLibraryAccessors getMapstruct() {
        return laccForMapstructLibraryAccessors;
    }

    /**
     * Returns the group of libraries at mockito
     */
    public MockitoLibraryAccessors getMockito() {
        return laccForMockitoLibraryAccessors;
    }

    /**
     * Returns the group of libraries at nimbus
     */
    public NimbusLibraryAccessors getNimbus() {
        return laccForNimbusLibraryAccessors;
    }

    /**
     * Returns the group of libraries at rest
     */
    public RestLibraryAccessors getRest() {
        return laccForRestLibraryAccessors;
    }

    /**
     * Returns the group of libraries at slf4j
     */
    public Slf4jLibraryAccessors getSlf4j() {
        return laccForSlf4jLibraryAccessors;
    }

    /**
     * Returns the group of libraries at spring
     */
    public SpringLibraryAccessors getSpring() {
        return laccForSpringLibraryAccessors;
    }

    /**
     * Returns the group of libraries at springdoc
     */
    public SpringdocLibraryAccessors getSpringdoc() {
        return laccForSpringdocLibraryAccessors;
    }

    /**
     * Returns the group of libraries at testcontainers
     */
    public TestcontainersLibraryAccessors getTestcontainers() {
        return laccForTestcontainersLibraryAccessors;
    }

    /**
     * Returns the group of libraries at uuid
     */
    public UuidLibraryAccessors getUuid() {
        return laccForUuidLibraryAccessors;
    }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class FlywayLibraryAccessors extends SubDependencyFactory {
        private final FlywayDatabaseLibraryAccessors laccForFlywayDatabaseLibraryAccessors = new FlywayDatabaseLibraryAccessors(owner);

        public FlywayLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (org.flywaydb:flyway-core)
         * with versionRef 'flyway'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("flyway.core");
        }

        /**
         * Returns the group of libraries at flyway.database
         */
        public FlywayDatabaseLibraryAccessors getDatabase() {
            return laccForFlywayDatabaseLibraryAccessors;
        }

    }

    public static class FlywayDatabaseLibraryAccessors extends SubDependencyFactory {

        public FlywayDatabaseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for postgresql (org.flywaydb:flyway-database-postgresql)
         * with versionRef 'flyway'.
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
         * Returns the group of libraries at jackson.datatype
         */
        public JacksonDatatypeLibraryAccessors getDatatype() {
            return laccForJacksonDatatypeLibraryAccessors;
        }

        /**
         * Returns the group of libraries at jackson.module
         */
        public JacksonModuleLibraryAccessors getModule() {
            return laccForJacksonModuleLibraryAccessors;
        }

    }

    public static class JacksonDatatypeLibraryAccessors extends SubDependencyFactory {

        public JacksonDatatypeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for hibernate (com.fasterxml.jackson.datatype:jackson-datatype-hibernate)
         * with versionRef 'jackson'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getHibernate() {
                return create("jackson.datatype.hibernate");
        }

            /**
             * Creates a dependency provider for jsr310 (com.fasterxml.jackson.datatype:jackson-datatype-jsr310)
         * with versionRef 'jackson'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJsr310() {
                return create("jackson.datatype.jsr310");
        }

    }

    public static class JacksonModuleLibraryAccessors extends SubDependencyFactory {

        public JacksonModuleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for kotlin (com.fasterxml.jackson.module:jackson-module-kotlin)
         * with versionRef 'jacksonKotlin'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getKotlin() {
                return create("jackson.module.kotlin");
        }

    }

    public static class JakartaLibraryAccessors extends SubDependencyFactory {

        public JakartaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for mail (com.sun.mail:jakarta.mail)
         * with versionRef 'jakartaMail'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getMail() {
                return create("jakarta.mail");
        }

    }

    public static class JjwtLibraryAccessors extends SubDependencyFactory {

        public JjwtLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for api (io.jsonwebtoken:jjwt-api)
         * with versionRef 'jjwt'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getApi() {
                return create("jjwt.api");
        }

            /**
             * Creates a dependency provider for impl (io.jsonwebtoken:jjwt-impl)
         * with versionRef 'jjwt'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getImpl() {
                return create("jjwt.impl");
        }

            /**
             * Creates a dependency provider for jackson (io.jsonwebtoken:jjwt-jackson)
         * with versionRef 'jjwt'.
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
         * Returns the group of libraries at junit.jupiter
         */
        public JunitJupiterLibraryAccessors getJupiter() {
            return laccForJunitJupiterLibraryAccessors;
        }

    }

    public static class JunitJupiterLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public JunitJupiterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jupiter (org.junit.jupiter:junit-jupiter)
         * with versionRef 'junit'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() {
                return create("junit.jupiter");
        }

            /**
             * Creates a dependency provider for params (org.junit.jupiter:junit-jupiter-params)
         * with versionRef 'junit'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getParams() {
                return create("junit.jupiter.params");
        }

    }

    public static class KotlinLibraryAccessors extends SubDependencyFactory {

        public KotlinLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for reflect (org.jetbrains.kotlin:kotlin-reflect)
         * with versionRef 'kotlin'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getReflect() {
                return create("kotlin.reflect");
        }

            /**
             * Creates a dependency provider for stdlib (org.jetbrains.kotlin:kotlin-stdlib)
         * with versionRef 'kotlin'.
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
         * Returns the group of libraries at kotlinx.coroutines
         */
        public KotlinxCoroutinesLibraryAccessors getCoroutines() {
            return laccForKotlinxCoroutinesLibraryAccessors;
        }

        /**
         * Returns the group of libraries at kotlinx.serialization
         */
        public KotlinxSerializationLibraryAccessors getSerialization() {
            return laccForKotlinxSerializationLibraryAccessors;
        }

    }

    public static class KotlinxCoroutinesLibraryAccessors extends SubDependencyFactory {

        public KotlinxCoroutinesLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (org.jetbrains.kotlinx:kotlinx-coroutines-core)
         * with version '1.7.3'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("kotlinx.coroutines.core");
        }

            /**
             * Creates a dependency provider for reactor (org.jetbrains.kotlinx:kotlinx-coroutines-reactor)
         * with version '1.7.3'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getReactor() {
                return create("kotlinx.coroutines.reactor");
        }

    }

    public static class KotlinxSerializationLibraryAccessors extends SubDependencyFactory {

        public KotlinxSerializationLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for json (org.jetbrains.kotlinx:kotlinx-serialization-json)
         * with versionRef 'kotlinxSerialization'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJson() {
                return create("kotlinx.serialization.json");
        }

    }

    public static class LettuceLibraryAccessors extends SubDependencyFactory {

        public LettuceLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (io.lettuce:lettuce-core)
         * with versionRef 'lettuce'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("lettuce.core");
        }

    }

    public static class LogbackLibraryAccessors extends SubDependencyFactory {

        public LogbackLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for classic (ch.qos.logback:logback-classic)
         * with versionRef 'logback'.
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
             * Creates a dependency provider for lombok (org.projectlombok:lombok)
         * with versionRef 'lombok'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() {
                return create("lombok");
        }

        /**
         * Returns the group of libraries at lombok.mapstruct
         */
        public LombokMapstructLibraryAccessors getMapstruct() {
            return laccForLombokMapstructLibraryAccessors;
        }

    }

    public static class LombokMapstructLibraryAccessors extends SubDependencyFactory {

        public LombokMapstructLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for binding (org.projectlombok:lombok-mapstruct-binding)
         * with version '0.2.0'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getBinding() {
                return create("lombok.mapstruct.binding");
        }

    }

    public static class MapstructLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public MapstructLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for mapstruct (org.mapstruct:mapstruct)
         * with versionRef 'mapstruct'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() {
                return create("mapstruct");
        }

            /**
             * Creates a dependency provider for processor (org.mapstruct:mapstruct-processor)
         * with versionRef 'mapstructProcessor'.
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
             * Creates a dependency provider for core (org.mockito:mockito-core)
         * with versionRef 'mockito'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("mockito.core");
        }

        /**
         * Returns the group of libraries at mockito.junit
         */
        public MockitoJunitLibraryAccessors getJunit() {
            return laccForMockitoJunitLibraryAccessors;
        }

    }

    public static class MockitoJunitLibraryAccessors extends SubDependencyFactory {

        public MockitoJunitLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jupiter (org.mockito:mockito-junit-jupiter)
         * with versionRef 'mockito'.
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
         * Returns the group of libraries at nimbus.jose
         */
        public NimbusJoseLibraryAccessors getJose() {
            return laccForNimbusJoseLibraryAccessors;
        }

    }

    public static class NimbusJoseLibraryAccessors extends SubDependencyFactory {

        public NimbusJoseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jwt (com.nimbusds:nimbus-jose-jwt)
         * with versionRef 'nimbusJoseJwt'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJwt() {
                return create("nimbus.jose.jwt");
        }

    }

    public static class RestLibraryAccessors extends SubDependencyFactory {

        public RestLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for assured (io.rest-assured:rest-assured)
         * with versionRef 'restAssured'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAssured() {
                return create("rest.assured");
        }

    }

    public static class Slf4jLibraryAccessors extends SubDependencyFactory {

        public Slf4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for api (org.slf4j:slf4j-api)
         * with versionRef 'slf4j'.
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
         * Returns the group of libraries at spring.boot
         */
        public SpringBootLibraryAccessors getBoot() {
            return laccForSpringBootLibraryAccessors;
        }

        /**
         * Returns the group of libraries at spring.security
         */
        public SpringSecurityLibraryAccessors getSecurity() {
            return laccForSpringSecurityLibraryAccessors;
        }

    }

    public static class SpringBootLibraryAccessors extends SubDependencyFactory {
        private final SpringBootStarterLibraryAccessors laccForSpringBootStarterLibraryAccessors = new SpringBootStarterLibraryAccessors(owner);

        public SpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at spring.boot.starter
         */
        public SpringBootStarterLibraryAccessors getStarter() {
            return laccForSpringBootStarterLibraryAccessors;
        }

    }

    public static class SpringBootStarterLibraryAccessors extends SubDependencyFactory {
        private final SpringBootStarterDataLibraryAccessors laccForSpringBootStarterDataLibraryAccessors = new SpringBootStarterDataLibraryAccessors(owner);

        public SpringBootStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for actuator (org.springframework.boot:spring-boot-starter-actuator)
         * with versionRef 'springBoot'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getActuator() {
                return create("spring.boot.starter.actuator");
        }

            /**
             * Creates a dependency provider for mail (org.springframework.boot:spring-boot-starter-mail)
         * with versionRef 'springBoot'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getMail() {
                return create("spring.boot.starter.mail");
        }

            /**
             * Creates a dependency provider for security (org.springframework.boot:spring-boot-starter-security)
         * with versionRef 'springBoot'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getSecurity() {
                return create("spring.boot.starter.security");
        }

            /**
             * Creates a dependency provider for test (org.springframework.boot:spring-boot-starter-test)
         * with versionRef 'springBoot'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTest() {
                return create("spring.boot.starter.test");
        }

            /**
             * Creates a dependency provider for validation (org.springframework.boot:spring-boot-starter-validation)
         * with versionRef 'springBoot'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getValidation() {
                return create("spring.boot.starter.validation");
        }

            /**
             * Creates a dependency provider for web (org.springframework.boot:spring-boot-starter-web)
         * with versionRef 'springBoot'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getWeb() {
                return create("spring.boot.starter.web");
        }

            /**
             * Creates a dependency provider for websocket (org.springframework.boot:spring-boot-starter-websocket)
         * with versionRef 'springBoot'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getWebsocket() {
                return create("spring.boot.starter.websocket");
        }

        /**
         * Returns the group of libraries at spring.boot.starter.data
         */
        public SpringBootStarterDataLibraryAccessors getData() {
            return laccForSpringBootStarterDataLibraryAccessors;
        }

    }

    public static class SpringBootStarterDataLibraryAccessors extends SubDependencyFactory {

        public SpringBootStarterDataLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jpa (org.springframework.boot:spring-boot-starter-data-jpa)
         * with versionRef 'springBoot'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJpa() {
                return create("spring.boot.starter.data.jpa");
        }

            /**
             * Creates a dependency provider for redis (org.springframework.boot:spring-boot-starter-data-redis)
         * with versionRef 'springBoot'.
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
             * Creates a dependency provider for config (org.springframework.security:spring-security-config)
         * with versionRef 'springSecurity'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getConfig() {
                return create("spring.security.config");
        }

            /**
             * Creates a dependency provider for test (org.springframework.security:spring-security-test)
         * with versionRef 'springSecurity'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTest() {
                return create("spring.security.test");
        }

            /**
             * Creates a dependency provider for web (org.springframework.security:spring-security-web)
         * with versionRef 'springSecurity'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getWeb() {
                return create("spring.security.web");
        }

        /**
         * Returns the group of libraries at spring.security.oauth2
         */
        public SpringSecurityOauth2LibraryAccessors getOauth2() {
            return laccForSpringSecurityOauth2LibraryAccessors;
        }

    }

    public static class SpringSecurityOauth2LibraryAccessors extends SubDependencyFactory {
        private final SpringSecurityOauth2ResourceLibraryAccessors laccForSpringSecurityOauth2ResourceLibraryAccessors = new SpringSecurityOauth2ResourceLibraryAccessors(owner);

        public SpringSecurityOauth2LibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jose (org.springframework.security:spring-security-oauth2-jose)
         * with versionRef 'springSecurity'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJose() {
                return create("spring.security.oauth2.jose");
        }

        /**
         * Returns the group of libraries at spring.security.oauth2.resource
         */
        public SpringSecurityOauth2ResourceLibraryAccessors getResource() {
            return laccForSpringSecurityOauth2ResourceLibraryAccessors;
        }

    }

    public static class SpringSecurityOauth2ResourceLibraryAccessors extends SubDependencyFactory {

        public SpringSecurityOauth2ResourceLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for server (org.springframework.security:spring-security-oauth2-resource-server)
         * with versionRef 'springSecurity'.
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
         * Returns the group of libraries at springdoc.openapi
         */
        public SpringdocOpenapiLibraryAccessors getOpenapi() {
            return laccForSpringdocOpenapiLibraryAccessors;
        }

    }

    public static class SpringdocOpenapiLibraryAccessors extends SubDependencyFactory {
        private final SpringdocOpenapiStarterLibraryAccessors laccForSpringdocOpenapiStarterLibraryAccessors = new SpringdocOpenapiStarterLibraryAccessors(owner);

        public SpringdocOpenapiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for common (org.springdoc:springdoc-openapi-common)
         * with versionRef 'springdocCommon'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCommon() {
                return create("springdoc.openapi.common");
        }

        /**
         * Returns the group of libraries at springdoc.openapi.starter
         */
        public SpringdocOpenapiStarterLibraryAccessors getStarter() {
            return laccForSpringdocOpenapiStarterLibraryAccessors;
        }

    }

    public static class SpringdocOpenapiStarterLibraryAccessors extends SubDependencyFactory {

        public SpringdocOpenapiStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for webmvc (org.springdoc:springdoc-openapi-starter-webmvc-ui)
         * with versionRef 'springdoc'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getWebmvc() {
                return create("springdoc.openapi.starter.webmvc");
        }

    }

    public static class TestcontainersLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {
        private final TestcontainersJunitLibraryAccessors laccForTestcontainersJunitLibraryAccessors = new TestcontainersJunitLibraryAccessors(owner);

        public TestcontainersLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for testcontainers (org.testcontainers:testcontainers)
         * with versionRef 'testcontainers'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() {
                return create("testcontainers");
        }

            /**
             * Creates a dependency provider for postgresql (org.testcontainers:postgresql)
         * with versionRef 'testcontainers'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getPostgresql() {
                return create("testcontainers.postgresql");
        }

        /**
         * Returns the group of libraries at testcontainers.junit
         */
        public TestcontainersJunitLibraryAccessors getJunit() {
            return laccForTestcontainersJunitLibraryAccessors;
        }

    }

    public static class TestcontainersJunitLibraryAccessors extends SubDependencyFactory {

        public TestcontainersJunitLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jupiter (org.testcontainers:junit-jupiter)
         * with versionRef 'testcontainers'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJupiter() {
                return create("testcontainers.junit.jupiter");
        }

    }

    public static class UuidLibraryAccessors extends SubDependencyFactory {

        public UuidLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for creator (com.github.stephenc.jcip:jcip-annotations)
         * with version '1.0-1'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCreator() {
                return create("uuid.creator");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: assertj (3.25.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAssertj() { return getVersion("assertj"); }

            /**
             * Returns the version associated to this alias: bcrypt (0.10.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getBcrypt() { return getVersion("bcrypt"); }

            /**
             * Returns the version associated to this alias: flyway (10.17.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getFlyway() { return getVersion("flyway"); }

            /**
             * Returns the version associated to this alias: gradle (8.5)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getGradle() { return getVersion("gradle"); }

            /**
             * Returns the version associated to this alias: hikaricp (5.1.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getHikaricp() { return getVersion("hikaricp"); }

            /**
             * Returns the version associated to this alias: jackson (2.17.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJackson() { return getVersion("jackson"); }

            /**
             * Returns the version associated to this alias: jacksonKotlin (2.17.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJacksonKotlin() { return getVersion("jacksonKotlin"); }

            /**
             * Returns the version associated to this alias: jakartaMail (2.0.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJakartaMail() { return getVersion("jakartaMail"); }

            /**
             * Returns the version associated to this alias: jjwt (0.12.5)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJjwt() { return getVersion("jjwt"); }

            /**
             * Returns the version associated to this alias: junit (5.10.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJunit() { return getVersion("junit"); }

            /**
             * Returns the version associated to this alias: kotlin (1.9.24)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKotlin() { return getVersion("kotlin"); }

            /**
             * Returns the version associated to this alias: kotlinxSerialization (1.6.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKotlinxSerialization() { return getVersion("kotlinxSerialization"); }

            /**
             * Returns the version associated to this alias: lettuce (6.3.1.RELEASE)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLettuce() { return getVersion("lettuce"); }

            /**
             * Returns the version associated to this alias: liquibase (4.29.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLiquibase() { return getVersion("liquibase"); }

            /**
             * Returns the version associated to this alias: logback (1.5.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLogback() { return getVersion("logback"); }

            /**
             * Returns the version associated to this alias: lombok (1.18.32)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLombok() { return getVersion("lombok"); }

            /**
             * Returns the version associated to this alias: mapstruct (1.5.5.Final)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMapstruct() { return getVersion("mapstruct"); }

            /**
             * Returns the version associated to this alias: mapstructProcessor (1.5.5.Final)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMapstructProcessor() { return getVersion("mapstructProcessor"); }

            /**
             * Returns the version associated to this alias: mockito (5.11.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMockito() { return getVersion("mockito"); }

            /**
             * Returns the version associated to this alias: mockk (1.13.13)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMockk() { return getVersion("mockk"); }

            /**
             * Returns the version associated to this alias: nimbusJoseJwt (9.37.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getNimbusJoseJwt() { return getVersion("nimbusJoseJwt"); }

            /**
             * Returns the version associated to this alias: postgresql (42.7.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getPostgresql() { return getVersion("postgresql"); }

            /**
             * Returns the version associated to this alias: redis (3.2.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getRedis() { return getVersion("redis"); }

            /**
             * Returns the version associated to this alias: restAssured (5.4.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getRestAssured() { return getVersion("restAssured"); }

            /**
             * Returns the version associated to this alias: slf4j (2.0.13)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSlf4j() { return getVersion("slf4j"); }

            /**
             * Returns the version associated to this alias: springBoot (3.2.5)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringBoot() { return getVersion("springBoot"); }

            /**
             * Returns the version associated to this alias: springDataJpa (3.2.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringDataJpa() { return getVersion("springDataJpa"); }

            /**
             * Returns the version associated to this alias: springFramework (6.1.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringFramework() { return getVersion("springFramework"); }

            /**
             * Returns the version associated to this alias: springMail (6.1.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringMail() { return getVersion("springMail"); }

            /**
             * Returns the version associated to this alias: springSecurity (6.2.4)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringSecurity() { return getVersion("springSecurity"); }

            /**
             * Returns the version associated to this alias: springValidation (3.2.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringValidation() { return getVersion("springValidation"); }

            /**
             * Returns the version associated to this alias: springWeb (6.1.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringWeb() { return getVersion("springWeb"); }

            /**
             * Returns the version associated to this alias: springdoc (2.3.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringdoc() { return getVersion("springdoc"); }

            /**
             * Returns the version associated to this alias: springdocCommon (2.3.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringdocCommon() { return getVersion("springdocCommon"); }

            /**
             * Returns the version associated to this alias: testcontainers (1.19.8)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getTestcontainers() { return getVersion("testcontainers"); }

            /**
             * Returns the version associated to this alias: testcontainersJunit (1.19.8)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getTestcontainersJunit() { return getVersion("testcontainersJunit"); }

            /**
             * Returns the version associated to this alias: uuid (4.5.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
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
             * Creates a plugin provider for lombok to the plugin id 'io.freefair.lombok'
             * with versionRef 'lombok'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getLombok() { return createPlugin("lombok"); }

        /**
         * Returns the group of plugins at plugins.dependency
         */
        public DependencyPluginAccessors getDependency() {
            return paccForDependencyPluginAccessors;
        }

        /**
         * Returns the group of plugins at plugins.junit
         */
        public JunitPluginAccessors getJunit() {
            return paccForJunitPluginAccessors;
        }

        /**
         * Returns the group of plugins at plugins.kotlin
         */
        public KotlinPluginAccessors getKotlin() {
            return paccForKotlinPluginAccessors;
        }

        /**
         * Returns the group of plugins at plugins.spring
         */
        public SpringPluginAccessors getSpring() {
            return paccForSpringPluginAccessors;
        }

    }

    public static class DependencyPluginAccessors extends PluginFactory {

        public DependencyPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for dependency.management to the plugin id 'io.spring.dependency-management'
             * with version '1.1.6'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getManagement() { return createPlugin("dependency.management"); }

    }

    public static class JunitPluginAccessors extends PluginFactory {

        public JunitPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for junit.platform to the plugin id 'org.junit.platform.gradle.plugin'
             * with version '1.10.2'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getPlatform() { return createPlugin("junit.platform"); }

    }

    public static class KotlinPluginAccessors extends PluginFactory {

        public KotlinPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for kotlin.jpa to the plugin id 'org.jetbrains.kotlin.plugin.jpa'
             * with versionRef 'kotlin'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getJpa() { return createPlugin("kotlin.jpa"); }

            /**
             * Creates a plugin provider for kotlin.jvm to the plugin id 'org.jetbrains.kotlin.jvm'
             * with versionRef 'kotlin'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getJvm() { return createPlugin("kotlin.jvm"); }

            /**
             * Creates a plugin provider for kotlin.serialization to the plugin id 'org.jetbrains.kotlin.plugin.serialization'
             * with versionRef 'kotlin'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getSerialization() { return createPlugin("kotlin.serialization"); }

            /**
             * Creates a plugin provider for kotlin.spring to the plugin id 'org.jetbrains.kotlin.plugin.spring'
             * with versionRef 'kotlin'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getSpring() { return createPlugin("kotlin.spring"); }

    }

    public static class SpringPluginAccessors extends PluginFactory {

        public SpringPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for spring.boot to the plugin id 'org.springframework.boot'
             * with versionRef 'springBoot'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getBoot() { return createPlugin("spring.boot"); }

    }

}
