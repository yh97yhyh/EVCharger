from rest_framework.serializers import ModelSerializer
from .models import Stations

from rest_framework.viewsets import ModelViewSet
from rest_framework.filters import SearchFilter

class StationsSerializer(ModelSerializer):
    class Meta:
        model = Stations
        fields = '__all__'