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
from django.db.models import Q

class StationsQuerySet(ModelViewSet):
    queryset = Stations.objects.all()
    serializer_class = StationsSerializer

    def get_stations_by_addr(self, addr):
        queryset = super().get_queryset()
        queryset = queryset.filter(addr__contains=addr)
        return queryset


    #1 : DC차데모 (01, 03, 05, 06)  , 2 : AC완속 (02), 7 : AC3상 (03, 06, 07), 4: DC콤보 (04, 05, 06)
    def get_stations(self, lat, lng, filter1, filter2, filter4, filter7):
        queryset = Stations.objects.annotate(distance=(6371*ACos(Cos(Radians(float(lat)))*Cos(Radians('lat'))*Cos(Radians('lng')-Radians(float(lng)))
	            +Sin(Radians(float(lat)))*Sin(Radians('lat')))))
        queryset = queryset.filter(distance__lte = 3)

        querysetUnion = None
        if filter1 is not None or filter1 is True:
            queryset_filter_1 = queryset.filter(Q(chgerType = "01") | Q(chgerType = "03") | Q(chgerType = "05") | Q(chgerType = "06"))
            querysetUnion = querysetUnion.union(queryset_filter_1) if querysetUnion is not None else queryset_filter_1

        if filter2 is not None or filter2 is True:
            queryset_filter_2 = queryset.filter(chgerType = "02")
            querysetUnion =  querysetUnion.union(queryset_filter_2) if querysetUnion is not None else  queryset_filter_2   

        if filter4 is not None or filter4 is True:
            queryset_filter_4 = queryset.filter(Q(chgerType = "04") | Q(chgerType ="05") | Q(chgerType ="06"))
            querysetUnion =  querysetUnion.union(queryset_filter_4) if querysetUnion is not None else queryset_filter_4    

        if filter7 is not None or filter7 is True:
            queryset_filter_7 = queryset.filter(Q(chgerType = "03") | Q(chgerType ="06") | Q(chgerType ="07"))
            querysetUnion = querysetUnion.union(queryset_filter_7) if querysetUnion is not None else queryset_filter_7                
      
        return queryset if querysetUnion is None else querysetUnion

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
            if request.GET.get('addr') is None: 
                stations_queryset = Stations.objects.all()
            else: # addr
                addr = request.GET.get('addr')
                stations_queryset = StationsQuerySet().get_stations_by_addr(addr)

        else: # lat, lng
            lat = request.GET.get('lat')
            lng = request.GET.get('lng')
            filter1 = request.GET.get('filter1')
            filter2 = request.GET.get('filter2')
            filter4 = request.GET.get('filter4')
            filter7 = request.GET.get('filter7')
            stations_queryset = StationsQuerySet().get_stations(lat,lng,filter1,filter2,filter4,filter7)

        stations_queryset_serializer = StationsSerializer(stations_queryset, many=True)
        return Response(stations_queryset_serializer.data, status=status.HTTP_200_OK)



