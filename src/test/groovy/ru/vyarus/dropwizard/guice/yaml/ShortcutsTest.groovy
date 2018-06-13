package ru.vyarus.dropwizard.guice.yaml

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.module.context.ConfigurationContext
import ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBootstrap
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule
import ru.vyarus.dropwizard.guice.module.yaml.YamlConfigInspector
import ru.vyarus.dropwizard.guice.test.spock.UseGuiceyApp
import ru.vyarus.dropwizard.guice.yaml.support.ComplexGenericCase
import ru.vyarus.dropwizard.guice.yaml.support.NotUniqueSubConfig
import spock.lang.Specification

import javax.inject.Inject


/**
 * @author Vyacheslav Rusakov
 * @since 11.06.2018
 */
@UseGuiceyApp(App)
class ShortcutsTest extends Specification {

    @Inject
    Bootstrap bootstrap

    def "Check module shortcuts"() {

        when: "config with not unique custom type"
        def config = create(NotUniqueSubConfig)
        config.sub1 = new NotUniqueSubConfig.SubConfig(sub: "val")
        config.sub2 = new NotUniqueSubConfig.SubConfig()
        def res = YamlConfigInspector.inspect(bootstrap, config)
        def mod = new DropwizardAwareModule() {}
        mod.setYamlConfig(res)
        then:
        mod.configuration("not.exists") == null
        mod.configuration("sub1") != null
        mod.configuration("sub1.sub") != null
        mod.configuration(NotUniqueSubConfig.SubConfig) == null
        mod.configurations(NotUniqueSubConfig.SubConfig).size() == 2

        when: "config with unique custom type"
        config = create(ComplexGenericCase)
        config.sub = new ComplexGenericCase.SubImpl()
        res = YamlConfigInspector.inspect(bootstrap, config)
        mod = new DropwizardAwareModule() {}
        mod.setYamlConfig(res)
        then:
        mod.configuration("not.exists") == null
        mod.configuration("sub") != null
        mod.configuration("sub.smth") ==  "sample"
        mod.configuration(ComplexGenericCase.Sub) != null
        mod.configurations(ComplexGenericCase.Sub).size() == 1

    }

    def "Check bundle shortcuts"() {

        when: "config with not unique custom type"
        def config = create(NotUniqueSubConfig)
        config.sub1 = new NotUniqueSubConfig.SubConfig(sub: "val")
        config.sub2 = new NotUniqueSubConfig.SubConfig()
        def res = YamlConfigInspector.inspect(bootstrap, config)
        def context = new ConfigurationContext()
        context.yamlConfig = res
        def bundle = new GuiceyBootstrap(context, [])
        then:
        bundle.configuration("not.exists") == null
        bundle.configuration("sub1") != null
        bundle.configuration("sub1.sub") != null
        bundle.configuration(NotUniqueSubConfig.SubConfig) == null
        bundle.configurations(NotUniqueSubConfig.SubConfig).size() == 2

        when: "config with unique custom type"
        config = create(ComplexGenericCase)
        config.sub = new ComplexGenericCase.SubImpl()
        res = YamlConfigInspector.inspect(bootstrap, config)
        context = new ConfigurationContext()
        context.yamlConfig = res
        bundle = new GuiceyBootstrap(context, [])
        then:
        bundle.configuration("not.exists") == null
        bundle.configuration("sub") != null
        bundle.configuration("sub.smth") ==  "sample"
        bundle.configuration(ComplexGenericCase.Sub) != null
        bundle.configurations(ComplexGenericCase.Sub).size() == 1

    }

    private <T extends Configuration> T create(Class<T> type) {
        bootstrap.configurationFactoryFactory
                .create(type, bootstrap.validatorFactory.validator, bootstrap.objectMapper, "dw").build()
    }

    static class App extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(GuiceBundle.builder().build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {

        }
    }
}