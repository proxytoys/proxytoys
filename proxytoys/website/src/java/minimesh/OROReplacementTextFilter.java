/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 03.08.2005 by Jörg Schaible
 */
package minimesh;

import com.opensymphony.module.sitemesh.html.TextFilter;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.StringSubstitution;
import org.apache.oro.text.regex.Util;

public class OROReplacementTextFilter implements TextFilter {
    
    private final Pattern pattern;
    private final StringSubstitution substitution;
    
    public OROReplacementTextFilter(final String pattern, final String substitution) {
        try {
            this.pattern = new Perl5Compiler().compile(pattern, Perl5Compiler.MULTILINE_MASK);
        } catch (final MalformedPatternException e) {
            throw new RuntimeException(e.getMessage());
        }
        this.substitution = new Perl5Substitution(substitution);
    }

    public String filter(final String text) {
        final StringBuffer buffer = new StringBuffer();
        Util.substitute(buffer, new Perl5Matcher(), pattern, substitution, text, Util.SUBSTITUTE_ALL);
        return buffer.toString();
    }

    public static void main(String[] args) {
        System.out.println(new OROReplacementTextFilter("JIRA:(PTOYS\\-[0-9]+)", "<a href=\"http://jira.codehaus.org/browse/$1\">$1</a>").filter("foo JIRA:PTOYS-5 bar"));
    }
}
