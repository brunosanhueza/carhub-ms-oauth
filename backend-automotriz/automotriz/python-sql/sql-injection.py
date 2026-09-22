import requests

url = "http://100.57.64.185:8097/api/v3/automotriz"

automotrices = [
    {
        "nombreAutomotriz": "Toyota",
        "direccionAutomotriz": "Av. Principal 1234, Santiago"
    },
    {
        "nombreAutomotriz": "Subaru",
        "direccionAutomotriz": "Av. Las Condes 5678, Santiago"
    },
    {
        "nombreAutomotriz": "Mazda",
        "direccionAutomotriz": "Av. Providencia 910, Santiago"
    },
    {
        "nombreAutomotriz": "Honda",
        "direccionAutomotriz": "Av. Vitacura 1122, Santiago"
    },
    {
        "nombreAutomotriz": "Hyundai",
        "direccionAutomotriz": "Av. Kennedy 3344, Santiago"
    },
    {
        "nombreAutomotriz": "Kia",
        "direccionAutomotriz": "Av. Apoquindo 5566, Santiago"
    },
    {
        "nombreAutomotriz": "Nissan",
        "direccionAutomotriz": "Av. Irarrázaval 7788, Santiago"
    },
    {
        "nombreAutomotriz": "Volkswagen",
        "direccionAutomotriz": "Av. Libertador 9900, Santiago"
    },
    {
        "nombreAutomotriz": "Audi",
        "direccionAutomotriz": "Av. El Golf 1111, Santiago"
    },
    {
        "nombreAutomotriz": "Mitsubishi",
        "direccionAutomotriz": "Av. Tobalaba 2222, Santiago"
    }
]

for automotriz in automotrices:
    response = requests.post(url, json=automotriz)
    if response.status_code == 201:
        print(f"✅ {automotriz['nombreAutomotriz']} creada con id: {response.json()['idAutomotriz']}")
    else:
        print(f"❌ Error con {automotriz['nombreAutomotriz']}: {response.status_code}")