package org.exist.xquery.modules.dita;

import org.exist.xquery.XPathException;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.ValueSequence;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ivan Lagunov
 */
public class RunDitaOTFunctionTest {

    @Test
    public void testGood() throws XPathException {
        List<Sequence> input = getRequiredArgumentsSequence("good.dita");
        final ValueSequence properties = new ValueSequence(
                new StringValue("clean.temp=no"),
                new StringValue("args.debug=yes"),
                new StringValue("pdf.formatter=fop")
        );
        input.get(5).addAll(properties);
        Sequence result = RunDitaOTFunction.staticEval(input.toArray(new Sequence[0]), null);
        Assert.assertEquals(Sequence.EMPTY_SEQUENCE, result);
    }

    @Test(expected = XPathException.class)
    public void testBad() throws XPathException {
        List<Sequence> input = getRequiredArgumentsSequence("bad.dita");
        RunDitaOTFunction.staticEval(input.toArray(new Sequence[0]), null);
    }

    private List<Sequence> getRequiredArgumentsSequence(String filename) throws XPathException {
        String resourcesPath = new File("target/test-classes").getAbsolutePath();
        return Arrays.asList(new Sequence[]{
                new ValueSequence(new StringValue("pdf")),
                new ValueSequence(new StringValue(resourcesPath + "/" + filename)),
                new ValueSequence(new StringValue(resourcesPath + "/out")),
                new ValueSequence(new StringValue(System.getenv("DITA_HOME"))),
                new ValueSequence(new StringValue(resourcesPath + "/temp")),
                new ValueSequence()
        });
    }
}
