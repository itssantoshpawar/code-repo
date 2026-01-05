(: XQuery transformation for Inventory Update :)
declare variable $input external;

<TransformedInventory>
  <update>
    <productId>{$input/Inventory/productId/text()}</productId>
    <quantityChange>{$input/Inventory/quantity/text()}</quantityChange>
    <operation>{$input/Inventory/operation/text()}</operation>
    <originalTimestamp>{$input/Inventory/timestamp/text()}</originalTimestamp>
    <processedTimestamp>{current-dateTime()}</processedTimestamp>
    <status>PENDING_APPROVAL</status>
  </update>
</TransformedInventory>
