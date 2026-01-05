(: XQuery transformation for Order Processing :)
declare variable $input external;

<TransformedOrder>
  <header>
    <orderId>{$input/Order/orderId/text()}</orderId>
    <customerId>{$input/Order/customerId/text()}</customerId>
    <processedDate>{current-dateTime()}</processedDate>
  </header>
  <details>
    <orderDate>{$input/Order/orderDate/text()}</orderDate>
    <totalAmount>{$input/Order/totalAmount/text()}</totalAmount>
    <itemCount>{count($input/Order/items/item)}</itemCount>
  </details>
  <items>
    {
      for $item in $input/Order/items/item
      return
        <item>
          <productId>{$item/productId/text()}</productId>
          <quantity>{$item/quantity/text()}</quantity>
          <price>{$item/price/text()}</price>
          <subtotal>{$item/quantity * $item/price}</subtotal>
        </item>
    }
  </items>
</TransformedOrder>
