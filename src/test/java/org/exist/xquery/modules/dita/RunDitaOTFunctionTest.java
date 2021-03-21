package org.exist.xquery.modules.dita;

import org.exist.xquery.XPathException;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.ValueSequence;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * @author Ivan Lagunov
 */
public class RunDitaOTFunctionTest {

    @Test
    public void testGood() throws XPathException {
        Sequence input = getRequiredArgumentsSequence("good.dita");
        input.add(new StringValue("-Dclean.temp=no"));
        input.add(new StringValue("-Dargs.debug=yes"));
        input.add(new StringValue("-Dpdf.formatter=fop"));
        Sequence result = RunDitaOTFunction.staticEval(new Sequence[]{ input }, null);
        Assert.assertEquals(2, result.getItemCount());
        Assert.assertEquals("", result.getStringValue());
    }

    @Test
    public void testBad() throws XPathException {
        Sequence input = getRequiredArgumentsSequence("bad.dita");
        Sequence result = RunDitaOTFunction.staticEval(new Sequence[]{ input }, null);
        Assert.assertEquals(2, result.getItemCount());
        Assert.assertThat(result.itemAt(0).getStringValue(),
                containsString("[DOTJ066E][ERROR] No id attribute on topic type element topic. Using generated id "));
        Assert.assertThat(result.itemAt(1).getStringValue(),
                containsString("[pipeline]   XPTY0004: An error occurred matching pattern"));
    }

    @Test(expected = XPathException.class)
    public void testFailure() throws XPathException {
        Sequence input = getRequiredArgumentsSequence("failure.dita");
        RunDitaOTFunction.staticEval(new Sequence[]{ input }, null);
    }

    private Sequence getRequiredArgumentsSequence(String filename) throws XPathException {
        String resourcesPath = new File("target/test-classes").getAbsolutePath();
        Sequence input = new ValueSequence(true);
        input.add(new StringValue("-i"));
        input.add(new StringValue(resourcesPath + "/" + filename));
        input.add(new StringValue("-f"));
        input.add(new StringValue("pdf"));
        input.add(new StringValue("-o"));
        input.add(new StringValue(resourcesPath + "/out"));
        input.add(new StringValue("-temp"));
        input.add(new StringValue(resourcesPath + "/temp"));
        input.add(new StringValue("-Ddita.dir=" + System.getenv("DITA_HOME")));
        input.add(new StringValue("-Djava.awt.headless=true"));
        return input;
    }
}
