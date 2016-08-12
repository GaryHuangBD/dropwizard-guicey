package ru.vyarus.dropwizard.guice;

import com.google.inject.Stage;
import ru.vyarus.dropwizard.guice.module.context.option.Option;

/**
 * Guicey core options. In most cases, direct option definition is not required because all options are covered
 * with shortcut method in {@link ru.vyarus.dropwizard.guice.GuiceBundle.Builder}. Direct option definition
 * may be useful when option is dynamically resolved and so shortcut methods can't be used (will require builder
 * flow interruption and additional if statements).
 * <p>
 * Normally options are mostly useful for runtime configuration values access (e.g. check in some 3rd party
 * bundle what packages are configured for classpath scan).
 * <p>
 * Generally options are not limited to this enum and custom option enums may be used by 3rd party bundles.
 *
 * @author Vyacheslav Rusakov
 * @see Option for details
 * @see ru.vyarus.dropwizard.guice.module.context.option.Options for usage in guice services
 * @see ru.vyarus.dropwizard.guice.module.context.option.OptionsInfo for reporting
 * @since 09.08.2016
 */
public enum GuiceyOptions implements Option {

    /**
     * Packages for classpath scan. Not empty value indicates auto scan mode enabled.
     * Empty by default.
     *
     * @see GuiceBundle.Builder#enableAutoConfig(String...)
     */
    ScanPackages(String[].class, new String[0]),

    /**
     * Enables commands search in classpath and dynamic installation. Requires auto scan mode.
     * Disabled by default.
     *
     * @see GuiceBundle.Builder#searchCommands()
     */
    SearchCommands(Boolean.class, false),

    /**
     * Automatic {@linkplain ru.vyarus.dropwizard.guice.module.installer.CoreInstallersBundle core installers}
     * installation.
     * Enabled by default.
     *
     * @see GuiceBundle.Builder#noDefaultInstallers()
     */
    UseCoreInstallers(Boolean.class, true),

    /**
     * Recognize {@link ru.vyarus.dropwizard.guice.module.installer.bundle.GuiceyBundle} from installed
     * dropwizard bundles.
     * Disabled by default.
     *
     * @see GuiceBundle.Builder#configureFromDropwizardBundles()
     */
    ConfigureFromDropwizardBundles(Boolean.class, false),

    /**
     * Bind all direct interfaces implemented by configuration objects to configuration instance in guice context.
     * Disabled by default.
     *
     * @see GuiceBundle.Builder#bindConfigurationInterfaces()
     */
    BindConfigurationInterfaces(Boolean.class, false),

    /**
     * Guice injector stage used for injector creation.
     * Production by default.
     *
     * @see GuiceBundle.Builder#build(Stage)
     */
    InjectorStage(Stage.class, Stage.PRODUCTION);

    private Class<?> type;
    private Object value;

    <T> GuiceyOptions(final Class<T> type, final T value) {
        this.type = type;
        this.value = value;
    }


    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Object getDefaultValue() {
        return value;
    }
}
