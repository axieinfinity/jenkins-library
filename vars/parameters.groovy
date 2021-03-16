/* 
  author: namcxn
  date: Mar 02 00:00:00 +07 2021
 */

public inputParametersTest() {
    def parameters = [
            string(name: 'aws_region', defaultValue: "us-east-1"),
            choice(name: 'environment', choices: "dev\nqa\nprod")
    ]
    return parameters
}

return this;