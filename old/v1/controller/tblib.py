# TerraBungee
# (C) 2020 Saghetti
# tblib.py: library with objects representing the TerraBungee network

class Service:
    def __init__(self,service_id,status):
        self.service_id = service_id
        self.status = status

    def get_status(self):
        return self.status

    def set_status(self,status):
        self.status = status

    def get_id(self):
        return self.service_id
