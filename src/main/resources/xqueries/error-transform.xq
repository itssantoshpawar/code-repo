(: XQuery transformation for Error Handling :)
declare variable $input external;

<ErrorReport>
  <errorInfo>
    <timestamp>{current-dateTime()}</timestamp>
    <severity>HIGH</severity>
    <processed>true</processed>
  </errorInfo>
  <originalError>
    {$input/*}
  </originalError>
</ErrorReport>
