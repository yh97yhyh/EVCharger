from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from .serializers import StationsSerializer
from rest_framework import status
from .models import Stations

from rest_framework.viewsets import ModelViewSet
from rest_framework.filters import SearchFilter
import math
from django.db.models.functions import Radians
from django.db.models.functions import Cos
from django.db.models.functions import Sin
from django.db.models.functions import ACos

class StationsQuerySet(ModelViewSet):
    queryset = Stations.objects.all()
    serializer_class = StationsSerializer

    # def get_queryset(self, addr):
    #     queryset = super().get_queryset()
    #     queryset = queryset.filter(addr__contains=query)
    #     return queryset

    def get_stations(self, lat, lng):
        queryset = Stations.objects.annotate(distance=(6371*ACos(Cos(Radians(float(lat)))*Cos(Radians('lat'))*Cos(Radians('lng')-Radians(float(lng)))
	            +Sin(Radians(float(lat)))*Sin(Radians('lat')))))
        queryset = queryset.filter(distance__lte = 3)
        return queryset

# Create your views here.
class StationsView(APIView):
    # def get(self, request):
    #     if request.GET.get('query') is None:
    #             stations_queryset = Stations.objects.all()
    #     else:
    #         query = request.GET.get('query')
    #         stations_queryset = StationsQuerySet().get_queryset(query)

    #     stations_queryset_serializer = StationsSerializer(stations_queryset, many=True)
    #     return Response({'stations':stations_queryset_serializer.data,
    #                         'count':stations_queryset.count()}, status=status.HTTP_200_OK)
    def get(self, request):
        if request.GET.get('lat') is None or request.GET.get('lng') is None:
                stations_queryset = Stations.objects.all()
        else:
            lat = request.GET.get('lat')
            lng = request.GET.get('lng')
            stations_queryset = StationsQuerySet().get_stations(lat,lng)

        stations_queryset_serializer = StationsSerializer(stations_queryset, many=True)
        return Response(stations_queryset_serializer.data, status=status.HTTP_200_OK)



