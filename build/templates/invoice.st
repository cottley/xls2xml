<invoice>
  <meta>
    <sourcefilename>$xls2xml_sourcefilename$</sourcefilename>
    <sheetno>$xls2xml_sheetno$</sheetno>
  </meta>
  <invoicedate row="$invoicedate_xls2xml_row$" col="$invoicedate_xls2xml_col$">$invoicedate$</invoicedate>
  <invoicenumber row="$invoicenumber_xls2xml_row$" col="$invoicenumber_xls2xml_col$">$invoicenumber$</invoicenumber>
  <invoicebillto>
    <name row="$invoicebillto_line1_xls2xml_row$" col="$invoicebillto_line1_xls2xml_col$">$invoicebillto_line1$</name>
    <addressline1>$invoicebillto_line2$</addressline1>
    <addressline2>$invoicebillto_line3$</addressline2>
    <addressline3>$invoicebillto_line4$</addressline3>    
  </invoicebillto>
  <list>
    $productdetails:{ productdetail |
    <productdetail>
      <productid row="$productdetail.invoice_productid_xls2xml_row$" col="$productdetail.invoice_productid_xls2xml_col$">$productdetail.invoice_productid$</productid>
      <productdesc row="$productdetail.invoice_description_xls2xml_row$" col="$productdetail.invoice_description_xls2xml_col$">$productdetail.invoice_description$</productdesc>
    </productdetail>  
    }$
  </list>
</invoice>
