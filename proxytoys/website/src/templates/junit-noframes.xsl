<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes" encoding="iso-8859-1"/>
<xsl:decimal-format decimal-separator="." grouping-separator="," />
<xsl:template match="testsuites">
<html>
    <head>
        <title>Unit Test Results</title>
    </head>
    <body>
		<a name="top"><!--  --></a>
    	<xsl:call-template name="pageHeader"/>
        <xsl:call-template name="summary"/>
        <xsl:call-template name="classes"/>
    </body>
</html>
</xsl:template>
<xsl:template name="pageHeader">
</xsl:template>
<xsl:template name="summary">
	<h1>Summary</h1>
	<xsl:variable name="testCount" select="sum(testsuite/@tests)"/>
	<xsl:variable name="errorCount" select="sum(testsuite/@errors)"/>
	<xsl:variable name="failureCount" select="sum(testsuite/@failures)"/>
	<xsl:variable name="timeCount" select="sum(testsuite/@time)"/>
	<xsl:variable name="successRate" select="($testCount - $failureCount - $errorCount) div $testCount"/>
	<p>Note: <i>failures</i> are anticipated and checked for with assertions while <i>errors</i> are unanticipated.</p>
	<table width="100%">
		<tr>
	        <th>Tests</th>
	        <th>Failures</th>
	        <th>Errors</th>
	        <th>Success rate</th>
	        <th>Time</th>
		</tr>
		<tr>
	        <xsl:attribute name="class">
	             <xsl:choose>
                     <xsl:when test="$failureCount &gt; 0">Failure</xsl:when>
                     <xsl:when test="$errorCount &gt; 0">Error</xsl:when>
	             </xsl:choose>
	        </xsl:attribute>
	        <td><xsl:value-of select="$testCount"/></td>
	        <td><xsl:value-of select="$failureCount"/></td>
	        <td><xsl:value-of select="$errorCount"/></td>
	        <td>
                <xsl:call-template name="display-percent">
                    <xsl:with-param name="value" select="$successRate"/>
                </xsl:call-template>
	        </td>
	        <td>
                <xsl:call-template name="display-time">
                    <xsl:with-param name="value" select="$timeCount"/>
                </xsl:call-template>
	        </td>
		</tr>
	</table>
</xsl:template>
<xsl:template name="classes">
    <xsl:for-each select="testsuite">
        <xsl:sort select="@name"/>
        <h1><xsl:value-of select="@name"/></h1>
		<table>
        	<xsl:call-template name="testcase.test.header"/>
            <xsl:if test="./error">
                <tr class="Error">
	                <td colspan="4"><xsl:apply-templates select="./error"/></td>
                </tr>
            </xsl:if>
            <xsl:apply-templates select="./testcase" mode="print.test"/>
        </table>
	</xsl:for-each>
</xsl:template>
<xsl:template name="testcase.test.header">
	<tr>
	    <th>Name</th>
	    <th>Status</th>
	    <th width="80%">Type</th>
	    <th nowrap="nowrap">Time(s)</th>
	</tr>
</xsl:template>
<xsl:template match="testcase" mode="print.test">
    <tr valign="top">
        <xsl:attribute name="class">
            <xsl:choose>
                <xsl:when test="failure | error">Error</xsl:when>
            </xsl:choose>
        </xsl:attribute>
        <td><xsl:value-of select="@name"/></td>
        <xsl:choose>
            <xsl:when test="failure">
                <td>Failure</td>
                <td><xsl:apply-templates select="failure"/></td>
            </xsl:when>
            <xsl:when test="error">
                <td>Error</td>
                <td><xsl:apply-templates select="error"/></td>
            </xsl:when>
            <xsl:otherwise>
                <td>Success</td>
                <td></td>
            </xsl:otherwise>
       </xsl:choose>
        <td>
            <xsl:call-template name="display-time">
                <xsl:with-param name="value" select="@time"/>
            </xsl:call-template>
        </td>
    </tr>
</xsl:template>
<xsl:template match="failure">
    <xsl:call-template name="display-failures"/>
</xsl:template>
<xsl:template match="error">
    <xsl:call-template name="display-failures"/>
</xsl:template>
<xsl:template name="display-failures">
	<xsl:choose>
		<xsl:when test="not(@message)">N/A</xsl:when>
		<xsl:otherwise>
	        <xsl:value-of select="@message"/>
		</xsl:otherwise>
	</xsl:choose>
	<code><pre><xsl:value-of select="."/></pre></code>
</xsl:template>
<xsl:template name="display-time">
        <xsl:param name="value"/>
        <xsl:value-of select="format-number($value,'0.000')"/>
</xsl:template>
<xsl:template name="display-percent">
        <xsl:param name="value"/>
        <xsl:value-of select="format-number($value,'0.00%')"/>
</xsl:template>
</xsl:stylesheet>
