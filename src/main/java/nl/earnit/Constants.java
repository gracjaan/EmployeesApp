package nl.earnit;

public class Constants {
    public final static String ABSOLUTE_URL = "/earnit";

    // 12 Hours
    public final static int TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 12;

    private static final String LOGO = """
        data:image/png;base64,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""";
    public final static String INVOICE_TEMPLATE = """
        <!DOCTYPE html>
        <html lang="en">
        	<head>
        		<meta charset="UTF-8" />
        		<style>
        			.logo {
        				position: relative;
        				height: 10rem;
        				margin: 3rem 0;
        			}
        			.logo img {
        				display: block;
        				height: 10rem;
        				position: absolute;
        				left: 50%;
        				transform: translateX(-50%);
        			}
        			.text {
        				margin-top: 2px;
        				margin-bottom: 2px;
        				font-family: sans-serif;
        			}
        			.text-left {
        				margin-top: 2px;
        				margin-bottom: 2px;
        				margin-right: 3rem;
        				text-align: right;
        				font-family: sans-serif;
        			}
                
        			.container p {
        				display: inline-block;
        			}
                
        			.container p:first-child {
        				width: 70%;
        			}
        			
        			.description {
        			    width: 50%;
        			    text-align: justify;
        			}
        		</style>
        	</head>
        	<body>
        		<div class="logo">
        			<img src="<earnitlogo>" />
        		</div>
        		<div>
        			<p class="text">&lt;company_name&gt;</p>
        			<p class="text">&lt;company_address&gt;</p>
        			<p class="text">KvK nummer: &lt;company_kvk&gt;</p>
                
        			<p class="text-left">Factuurnummer: &lt;invoice_number&gt;</p>
        			<p class="text-left">Factuurdatum: &lt;date&gt;</p>
                
        			<br /><br /><br />
        			<p class="text">&lt;student_name&gt;</p>
        			<p class="text">&lt;student_address&gt;</p>
        			<p class="text">KvK nummer: &lt;student_kvk&gt;</p>
        			<p class="text">BTW nummer: &lt;student_btw&gt;</p>
                
        			<br />
        			<hr style="background-color: black; height: 0.6rem" />
                
        			<br />
        			<div>
        				<div class="container">
        					<p class="grid-item text">Weeknummer</p>
        					<p class="grid-item text">&lt;week_number&gt;</p>
        				</div>
                
        				<p class="grid-item"></p>
        				<p class="grid-item"></p>
                
        				<div class="container">
        					<p class="grid-item text">&lt;role&gt;,</p>
        					<p class="grid-item text">€&lt;value&gt;</p>
        				</div>
        				<p class="grid-item text description">&lt;description&gt;</p>
                
        				<p class="grid-item text"></p>
        				<p class="grid-item"></p>
        				<p class="grid-item"></p>
                
        				<p class="grid-item text">Totaal Bedrag excl. BTW</p>
                
        				<p class="grid-item text"></p>
                
        				<div class="container">
        					<p class="grid-item text">BTW 21%</p>
        					<p class="grid-item text">€&lt;tax_value&gt;</p>
        				</div>
        			</div>
        			<br />
        			<hr style="background-color: black; height: 0.2rem" />
        			<br />
                
        			<div class="container">
        				<p class="grid-item text">Eindbedrag</p>
        				<p class="grid-item text">€&lt;total_value&gt;</p>
        			</div>
        			<hr style="background-color: black; height: 0.6rem" />
        		</div>
        	</body>
        </html>
        """.replace("<earnitlogo>", LOGO);
}
