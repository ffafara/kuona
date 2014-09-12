{
  "number":     <build.Number>
  "duration":   <build.Duration>
  "result":     <build.Result>
  "building":   <build.isBuilding>
  "description":"<build.Description>"
  "name":       "<build.FullDisplayName>"
  "id":         <build.id>
  "timestamp":  <build.timestamp>
  "result":     <build.result>
  "actions":    [
            <build.actions:{action |
             <action: {k,v | "<k>" "<v>",
              }>}>
  ]<if(build.parameters)>,
  "parameters": {
            <build.parameters:{p | <p>}>
  }
  <endif>
}
