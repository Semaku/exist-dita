package org.exist.xquery.modules.dita;

import org.exist.xquery.AbstractInternalModule;
import org.exist.xquery.FunctionDef;

import java.util.List;
import java.util.Map;

/**
 * A module for DITA processing.
 *
 * @author Ivan Lagunov
 */
public class DitaModule extends AbstractInternalModule {

    static final String NAMESPACE_URI = "http://exist-db.org/xquery/dita";
    static final String PREFIX = "dita";
    private final static String RELEASED_IN_VERSION = "eXist-2.1";

    private static final FunctionDef[] functions = {
            new FunctionDef(RunDitaOTFunction.signature, RunDitaOTFunction.class)
    };

    public DitaModule(Map<String, List<?>> parameters) {
        super(functions, parameters);
    }

    @Override
    public String getNamespaceURI() {
        return NAMESPACE_URI;
    }

    @Override
    public String getDefaultPrefix() {
        return PREFIX;
    }

    @Override
    public String getDescription() {
        return "A module for DITA processing";
    }

    @Override
    public String getReleaseVersion() {
        return RELEASED_IN_VERSION;
    }
}
