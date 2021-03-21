package org.exist.xquery.modules.dita;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.exist.dom.QName;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A function to run DITA OT processing.
 *
 * @author Ivan Lagunov
 */
public class RunDitaOTFunction extends BasicFunction {

    protected static final Logger LOG = Logger.getLogger(RunDitaOTFunction.class);

    public final static String DITA_HOME = "DITA_HOME";
    public final static String DITA_DIR = System.getenv(DITA_HOME);
    public final static String DITA_EXECUTABLE = DITA_DIR + File.separatorChar + "bin" + File.separatorChar +
            "dita" + (SystemUtils.IS_OS_WINDOWS ? ".bat" : "");

    public final static FunctionSignature signature = new FunctionSignature(
            new QName("run-dita-ot", DitaModule.NAMESPACE_URI, DitaModule.PREFIX),
            "A function to run DITA OT processing.",
            new SequenceType[]{
                    new FunctionParameterSequenceType("parameters", Type.STRING, Cardinality.ZERO_OR_MORE,
                            "The parameters in format 'ant-parameter-name=value'. " +
                                    "The documentation is available here: https://www.dita-ot.org/3.6/parameters/parameters_intro.html"
                    )
            },
            new FunctionReturnSequenceType(Type.STRING, Cardinality.MANY, "DITA-OT error and debug output as a sequence of 2 strings")
    );

    public RunDitaOTFunction(XQueryContext context) {
        super(context, signature);
    }

    @Override
    public Sequence eval(Sequence[] sequences, Sequence sequence) throws XPathException {
        return staticEval(sequences, sequence);
    }

    protected static Sequence staticEval(Sequence[] sequences, Sequence sequence) throws XPathException {
        if (null == DITA_DIR) {
            String errorMessage = DITA_HOME + " environmental variable is not found";
            LOG.fatal(errorMessage);
            throw new XPathException(errorMessage);
        }

        LOG.info("Running DITA OT with parameters: " + Arrays.toString(sequences));
        ProcessBuilder pb = new ProcessBuilder(getDitaCommand(sequences)).directory(new File(DITA_DIR));
        try {
            Process process = pb.start();
            String errorOutput = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(errorOutput)) LOG.error(errorOutput);
            String debugOutput = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(debugOutput)) LOG.debug(debugOutput);
            LOG.info("Completed DITA OT processing");
            if (process.exitValue() != 0) throw new XPathException(errorOutput);
            return new ValueSequence(new StringValue(errorOutput), new StringValue(debugOutput));
        } catch (IOException e) {
            LOG.error("DITA OT process failed", e);
            throw new XPathException(e.getLocalizedMessage());
        }
    }

    private static String[] getDitaCommand(Sequence[] sequences) throws XPathException {
        List<String> args = new ArrayList<>();

        args.add(DITA_EXECUTABLE);
        if (!sequences[0].isEmpty()) {
            SequenceIterator iterator = sequences[0].iterate();
            while (iterator.hasNext()) {
                args.add(iterator.nextItem().getStringValue());
            }
        }

        return args.toArray(new String[0]);
    }
}
