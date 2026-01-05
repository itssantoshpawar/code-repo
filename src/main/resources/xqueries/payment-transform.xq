(: XQuery transformation for Payment Processing :)
declare variable $input external;

<TransformedPayment>
  <paymentInfo>
    <id>{$input/Payment/paymentId/text()}</id>
    <orderId>{$input/Payment/orderId/text()}</orderId>
    <processedTimestamp>{current-dateTime()}</processedTimestamp>
  </paymentInfo>
  <transaction>
    <amount>{$input/Payment/amount/text()}</amount>
    <method>{$input/Payment/paymentMethod/text()}</method>
    <originalTimestamp>{$input/Payment/timestamp/text()}</originalTimestamp>
    <status>PROCESSED</status>
  </transaction>
</TransformedPayment>
